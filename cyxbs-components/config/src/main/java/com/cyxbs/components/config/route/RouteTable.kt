package com.cyxbs.components.config.route

/**
 * 路由表命名规则：
 *
 * 1、常量名（全大写）：模块名_功能描述，例：QA_ENTRY
 * 2、二级路由：/模块名/功能描述，例：/qa/entry
 * 3、多级路由：/模块依赖关系倒置/功能描述，例：/map/discover/entry
 *
 * 注意：如果你的 api 模块只有父模块实现，请把路由地址放在你的 api 模块中！！！
 *     这里只放共用的路由地址
 */