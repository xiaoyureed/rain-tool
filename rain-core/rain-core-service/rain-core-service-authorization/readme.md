## oauth2

``` 
- authentication code: Client app先向 auth server 请求 auth code, 然后用 auth code 向 auth server 获取 access token
    最安全, 适用于 client app 有后台服务器的情况, 请求 Token 的过程完全发生在后台
- password 模式: 直接将 资源服务器的 username/passwor 告诉 Client app, 向 auth server 申请 Token
    最不安全, 仅适用于信任 Client app 的情况
- implicit: Client app 是纯前端应用, 直接从 auth server 获取 access token
- client credentials 模式: 类似 implicit 模式, 只是 Client App 有后台, 获取 access token 的过程发生在后台

                                                           +--------------------------------+                                                                                          
                             同意颁发 Token                 |                                |                                                                                          
+--------------+                                           |    +--------------------+      |                                                                                          
|              | ------------------------------------------|--> |                    |      |                                                                                          
|  (User)      |                                           |    |                    |      |                                                                                          
|  Brower      |                                           |    |   Auth Server      |      |                                                                                          
|              |                                           |    |                    |      |                                                                                          
|              |             +-----------------------------|----|                    |      |                                                                                          
+--------------+             |     生成 Access Token        |    |                    |      |                                                                                          
                             |                             |    +--------------------+      |                                                                                          
     | |                     |                             |                                |                                                                                          
     | |  获取资源            |                             |                                 |                                                                                          
     | |                     |                             |          微信/Github            |                                                                                          
     | |                     |                             |                                |                                                                                          
     | |                     v                             |                                |                                                                                          
     | |         +---------------+                         |    +---------------------+     |                                                                                          
     | +------>  |               |                         |    |                     |     |                                                                                          
     +-------->  |               |                         |    |                     |     |                                                                                          
                 |  Client App   |      携带 Token          |    |   Resource Server   |     |                                                                                          
                 |               |                         |    |                     |     |                                                                                          
                 |               | ------------------------|--> |                     |     |                                                                                          
                 |               |                         |    |                     |     |                                                                                          
                 +---------------+                         |    +---------------------+     |                                                                                          
                                                           |                                |                                                                                          
                                                           |                                |                                                                                          
                                                           |                                |                                                                                          
                                                           +--------------------------------+                                                                                          
```
