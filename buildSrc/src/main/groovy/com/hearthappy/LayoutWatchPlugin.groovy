package com.hearthappy

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

import java.nio.file.*

// 自定义任务类（继承 DefaultTask）
class LayoutWatchTask extends DefaultTask {
    // 布局文件目录（通过 Gradle 的 file() 方法动态获取，避免硬编码）
    @InputDirectory
    // 标记为输入目录（Gradle 会自动监听其变化）
    Path layoutDir = project.file("src/main/res/layout").toPath()  // 关键：使用 project.file() 解析路径

    @TaskAction
    // 标记为任务执行入口（必须）
    void watchLayout() {
        logger.lifecycle("start layouts listener... dir: ${layoutDir}")
        if (!layoutDir.toFile().exists()) {
            logger.error("layouts file not exists: ${layoutDir}")
            return
        }

        // 使用守护线程（Daemon Thread），允许 Gradle 任务正常退出
        Thread listenerThread = new Thread({ ->
            WatchService watchService = FileSystems.getDefault().newWatchService()
            layoutDir.register(watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.OVERFLOW  // 处理事件队列溢出
            )

            try {
                while (true) {
                    WatchKey key = watchService.take()
                    key.pollEvents().each { WatchEvent<?> event ->
                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            Path modifiedFilePath = layoutDir.resolve(event.context() as Path)
                            logger.lifecycle("Layout file modification detected : ${modifiedFilePath}")
                            logger.lifecycle("update data : ${new Date()}")
                            triggerKspTask(project)
                        }
                    }
                    key.reset()
                }
            } finally {
                watchService.close()
                logger.info("service close!")
            }
        })
        listenerThread.setDaemon(true) // 关键：设置为守护线程，不阻塞 Gradle 退出
        listenerThread.start()

        // 任务立即返回，不阻塞 Gradle
        logger.lifecycle("The listening thread is already running in the background (daemon thread)")
    }

    private static void triggerKspTask(Project project) {
        // 触发 debug 变体的 KSP 任务
        def kspTask = project.tasks.findByName("kspDebugKotlin")
        if (kspTask) {
            kspTask.execute()
            project.logger.lifecycle("KSP 代码生成完成")
        } else {
            project.logger.error("KSP 任务未找到")
        }
    }
}


// 插件主类（实现 Plugin 接口）
class LayoutWatchPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.plugins.apply("com.google.devtools.ksp")  // 确保 KSP 插件已应用
        // 注册自定义任务（名称为 watchLayout）
        project.tasks.register("watchLayout", LayoutWatchTask) {
            group = "viewModelAutomation"  // 任务分组（在 Gradle 中可通过分组过滤）
            description = "监听 res/layout 目录下的布局文件修改并打印日志"
            // 绑定到 processResources 任务（布局文件属于资源文件）
//            project.tasks.getByName("processResources").dependsOn(task)
        }
    }
}