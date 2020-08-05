db.role.drop();
db.menu.drop();
db.user.drop();
db.role.insert([
{
    "_id" : ObjectId("5b4841bb72ea1d41ddd720b5"),
    "rolename" : "管理员",
    "rolekey" : "ROLE_ADMIN",
    "ctime" : ISODate("2018-07-13T06:07:55.164Z")
},
{
    "_id" : ObjectId("5b4841bb72ea1d41ddd720b6"),
    "rolename" : "普通用户",
    "rolekey" : "ROLE_USER",
    "ctime" : ISODate("2018-07-13T06:07:55.164Z")
},
{
    "_id" : ObjectId("5b4841bb72ea1d41ddd720b7"),
    "rolename" : "目诊用户",
    "rolekey" : "ROLE_EYE",
    "ctime" : ISODate("2018-07-13T06:07:55.164Z")
}
                
]);


db.menu.insert([
{
    "_id" : ObjectId("5b4841bb72ea1d41ddd720c1"),
    "name" : "系统管理",
    "isfristmenu" : "0",
    "icon" : "glyphicon glyphicon-cog",
    "pid" : "",
    "url" : "",
    "seq" : "1",
    "ctime" : ISODate("2018-07-13T06:07:55.164Z"),
    "roles" : [ 
        {
            "_id" : ObjectId("5b4841bb72ea1d41ddd720b5"),
            "rolename" : "管理员",
            "rolekey" : "ROLE_ADMIN",
            "ctime" : ISODate("2018-07-13T06:07:55.164Z")
        }
    ]
},

{
    "_id" : ObjectId("5b4841bb72ea1d41ddd720c4"),
    "name" : "统计报表",
    "isfristmenu" : "0",
    "icon" : "glyphicon glyphicon-stats",
    "pid" : "",
    "url" : "",
    "seq" : "4",
    "ctime" : ISODate("2018-07-13T06:07:55.164Z"),
    "roles" : [ 
        {
            "_id" : ObjectId("5b4841bb72ea1d41ddd720b5"),
            "rolename" : "管理员",
            "rolekey" : "ROLE_ADMIN",
            "ctime" : ISODate("2018-07-13T06:07:55.164Z")
        }
    ]
},
{
    "_id" : ObjectId("5b4841bb72ea1d41ddd720c5"),
    "name" : "用户管理",
    "isfristmenu" : "1",
    "icon" : "glyphicon glyphicon-user",
    "pid" : ObjectId("5b4841bb72ea1d41ddd720c1"),
    "url" : "/user/userlist.htm",
    "seq" : "1",
    "ctime" : ISODate("2018-07-13T06:07:55.164Z"),
    "roles" : [ 
        {
            "_id" : ObjectId("5b4841bb72ea1d41ddd720b5"),
            "rolename" : "管理员",
            "rolekey" : "ROLE_ADMIN",
            "ctime" : ISODate("2018-07-13T06:07:55.164Z")
        }
    ]
},
{
    "_id" : ObjectId("5b4841bb72ea1d41ddd720c6"),
    "name" : "角色管理",
    "isfristmenu" : "1",
    "icon" : "glyphicon glyphicon-edit",
    "pid" : ObjectId("5b4841bb72ea1d41ddd720c1"),
    "url" : "/role/rolelist.htm",
    "seq" : "2",
    "ctime" : ISODate("2018-07-13T06:07:55.164Z"),
    "roles" : [ 
        {
            "_id" : ObjectId("5b4841bb72ea1d41ddd720b5"),
            "rolename" : "管理员",
            "rolekey" : "ROLE_ADMIN",
            "ctime" : ISODate("2018-07-13T06:07:55.164Z")
        }
    ]
},
{
    "_id" : ObjectId("5b4841bb72ea1d41ddd720c8"),
    "name" : "菜单管理",
    "isfristmenu" : "1",
    "icon" : "glyphicon glyphicon-folder-open",
    "pid" : ObjectId("5b4841bb72ea1d41ddd720c1"),
    "url" : "/menu/menulist.htm",
    "seq" : "4",
    "ctime" : ISODate("2018-07-13T06:07:55.164Z"),
    "roles" : [ 
        {
            "_id" : ObjectId("5b4841bb72ea1d41ddd720b5"),
            "rolename" : "管理员",
            "rolekey" : "ROLE_ADMIN",
            "ctime" : ISODate("2018-07-13T06:07:55.164Z")
        }
    ]
},
{
    "_id" : ObjectId("5b4841bb72ea1d41ddd720cf"),
    "name" : "日志管理",
    "isfristmenu" : "1",
    "icon" : "glyphicon glyphicon-th",
    "pid" : ObjectId("5b4841bb72ea1d41ddd720c4"),
    "url" : "/manage/log/list.htm",
    "seq" : "11",
    "ctime" : ISODate("2018-07-13T06:07:55.164Z"),
    "roles" : [ 
        {
            "_id" : ObjectId("5b4841bb72ea1d41ddd720b5"),
            "rolename" : "管理员",
            "rolekey" : "ROLE_ADMIN",
            "ctime" : ISODate("2018-07-13T06:07:55.164Z")
        }
    ]
},
{
    "_id" : ObjectId("5bac7111da5b1b22b294700a"),
    "isfristmenu" : "1",
    "roles" : [ 
        {
            "rolename" : "管理员",
            "rolekey" : "ROLE_ADMIN",
            "ctime" : ISODate("2018-07-13T06:07:55.164Z"),
            "id" : "5b4841bb72ea1d41ddd720b5"
        }
    ],
    "name" : "登录管理",
    "pid" : ObjectId("5b4841bb72ea1d41ddd720c4"),
    "url" : "/loginlog/list.htm",
    "seq" : "",
    "ctime" : ISODate("2018-09-27T05:56:33.060Z")
}                
]);

db.user.insert([
{
    "_id" : ObjectId("5b4841bb72ea1d41ddd720b9"),
    "username" : "admin",
    "password" : "c5430dd00ec2cc60a495a0e0d8763e11",
    "salt" : "20181009135639",
    "roles" : [ 
        {
            "_id" : ObjectId("5b4841bb72ea1d41ddd720b5"),
            "rolename" : "管理员",
            "rolekey" : "ROLE_ADMIN",
            "ctime" : ISODate("2018-07-13T06:07:55.164Z")
        }, 
        {
            "_id" : ObjectId("5b4841bb72ea1d41ddd720b6"),
            "rolename" : "普通用户",
            "rolekey" : "ROLE_USER",
            "ctime" : ISODate("2018-07-13T06:07:55.164Z")
        }
    ],
    "ctime" : ISODate("2018-07-13T06:07:55.164Z")
},
{
    "_id" : ObjectId("5b4841bb72ea1d41ddd720ba"),
    "username" : "user",
    "salt" : "20181009135639",
    "password" : "842f3ccf87260b882c216452a51e87fc",
    "roles" : [ 
        {
            "_id" : ObjectId("5b4841bb72ea1d41ddd720b6"),
            "rolename" : "普通用户",
            "rolekey" : "ROLE_USER",
            "ctime" : ISODate("2018-07-13T06:07:55.164Z")
        }
    ],
    "ctime" : ISODate("2018-07-13T06:07:55.164Z")
}
])
