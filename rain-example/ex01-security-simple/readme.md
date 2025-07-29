```

/**
 * What does spring-security do by default?
 * - Authentication required for each request
 *  - Req without auth will be redirected to a welcome page
 * - generate a default account "user", with a password printing on the console
 * - Gen a default login/logout form page
 * - 401 unauthorized error for each api request
 */


                            +------------+                                                                                                                                        
                            |   client   |                                                                                                                                        
                            |            |                                                                                                                                        
                            +------------+                                                                                                                                        
                                 |                                                                                                                                                
                                 |                                                                                                                                                
                                 v                                                                                                                                                
                   +-------------------------------+                                                                                                                              
                   |    +----------+               |                                                                                                                              
      FilterChain  |    | filter0  |               |      将 spring 容器中的 filter 委托到这里                                                                                              
 (位于 Servlet容器) |    |          |      +------------>  交给 FilterChain 统一管理                                                                                                     
                   |    +----------+      |        |                                                                                                                              
                   |        v             |        |                                                                                                                              
                   |   +----------------------+    |             SecurityFilterChain  (/api/**)                                                                                   
                   |   |DelegatingFilterProxy |    |          +----------------------+                                                                                            
                   |   |+------------------+  |    |          | +--------------+     |                                                                                            
                   |   || FilterChainProxy | -------------->  | | filter0      |     |                                                                                            
                   |   |+------------------+  |    |    |     | +--------------+     |                                                                                            
                   |   +----------------------+    |    |     | +---------------+    |                                                                                            
                   |       v                       |    |     | | filter1       |    |                                                                                            
                   |   +------------------+        |    |     | +---------------+    |                                                                                            
                   |   |  filter2         |        |    |     | +---------------+    |                                                                                            
                   |   +------------------+        |    |     | |  ...          |    |                                                                                            
                   |   +-------------------+       |    |     | +---------------+    |                                                                                            
                   |   | fitler3           |       |    |     |                      |                                                                                            
                   |   +-------------------+       |    |     +----------------------+                                                                                            
                   +-------------------------------+    |                                                                                                                         
                               |                        |        +-----------------------+                                                                                        
                               |                        |        |                       |                                                                                        
                               v                        +----->  | SecurityFilterChain   | (/admin/**)                                                                            
                     +-------------------------+                 |                       |                                                                                        
                     |                         |                 +-----------------------+                                                                                        
                     |                         |                                                                                                                                  
                     |   Servlet               |                                                                                                                                  
                     |                         |                                                                                                                                  
                     +-------------------------+                                                                                                                                  
```
