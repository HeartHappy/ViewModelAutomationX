package com.hearthappy.vma.model

import java.io.IOException

/**
 * Created Date: 2025/5/28
 * @author ChenRui
 * ClassDescription：处理Http状态码200，但是后端封装的基础数据类型中的code不为200的情况
 */
class BusinessException(msg: String) : IOException(msg)