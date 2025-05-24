package com.hearthappy.annotations.storage

/**
 * Created Date: 2025/5/24
 * @author ChenRui
 * ClassDescription：对象关联
 * 声明该注解，即可遍历该对象的属性和注解，并生成DataStore文件
 * 默认情况下，声明DataStore注解，只会遍历该对象的基本属性类型和注解，不会遍历该对象的属性对象和注解
 */
annotation class ObjectRelation
