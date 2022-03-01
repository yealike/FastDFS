0.我的所有软件下载的目录都是在software目录下

```shell
mkdir -p /usr/local/fastdfs
cd /usr/local/fastdfs/
```

1.在Linux机器上安装gcc

```shell
 yum -y install cmake make gcc-c++
```

# 一、安装核心库

1.安装版本

```
#官网
https://github.com/happyfish100

fastdfs-6.06.tar.gz
libfastcommon-1.0.43.tar.gz
fastdfs-nginx-module-1.22.tar.gz
nginx-1.16.1.tar.gz
```

2.安装fastdfs的核心库,核心库的地址如下

```http
https://github.com/happyfish100/libfastcommon/archive/V1.0.7.tar.gz
```

如果你的linux主机连接github不受限制的话推荐命令

```http
wget https://github.com/happyfish100/libfastcommon/archive/V1.0.7.tar.gz
```

如果有限制，建议在Windows系统的浏览器中按url地址https://github.com/happyfish100/libfastcommon/archive/V1.0.7.tar.gz下载核心库的tar包

```shell
#解压当前压缩包
tar -zxvf libfastcommon-1.0.43.tar.gz
```

进入目录

```shell
cd libfastcommon-1.0.43/
#查看当前目录下有如下命令


[root@VM-0-12-centos libfastcommon-1.0.7]# ll
总用量 24
-rw-rw-r-- 1 root root 2170 9月  16 2014 HISTORY
-rw-rw-r-- 1 root root  582 9月  16 2014 INSTALL
-rw-rw-r-- 1 root root 1341 9月  16 2014 libfastcommon.spec
-rwxrwxr-x 1 root root 2151 9月  16 2014 make.sh
-rw-rw-r-- 1 root root  617 9月  16 2014 README
drwxrwxr-x 2 root root 4096 9月  16 2014 src

#采用make.sh编译一下
./make.sh
#采用./make.sh命令进行安装
./make.sh install
```

核心库的相关操作完成

3.1.4软连接创建

因为FastDFS主程序设置的目录是/usr/local/lib,所以我们需要创建软链接，改成我们想要的位置(相当于快捷方式)

```shell
ln -s /usr/lib64/libfastcommon.so /usr/local/lib/libfastcommon.so
ln -s /usr/local/lib64/libfdfsclient.so /usr/local/lib/libfdfsclient.so
ln -s /usr/local/lib64/libfdfsclient.so /usr/lib/libfdfsclient.so
```

# 二、安装FastDFS

2.1下载tar包

去github地址下载对应tar包https://github.com/happyfish100/fastdfs/tags我下载的tar包版本是6.0.6

![image-20220226112355534](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20220226112355534.png)

上传到linux服务器的指定目录

2.2解压tar包

```shell
tar -zxvf fastdfs-6.06.tar.gz
```

2.3进入目录执行安装流程

```shell
cd fastdfs-6.06
#修改.sh文件实现自定义安装位置
vim ./make.sh
#将默认目录改为如下目录
TARGET_PREFIX=$DESTDIR/usr/local
#保存并退出.
#编译源码
./make.sh
#安装
./make.sh install


```

安装后，FastDFS主程序所在的位置是

- /usr/local/bin可执行文件所在的位置，默认安装的位置是/usr/bin中。
- /etc/fdfs配置文件所在的位置，就是默认位置
- /usr/local/lib64主程序代码所在的位置，默认在/usr/bin中。
- /usr/local/include/fastdfs包含的一些插件组所在的位置，默认在/usr/include/fastdfs中

# 三、配置tracker

## 3.1配置tracker

```shell
#进入配置文件目录
cd /etc/fdfs
#配置之前先将源文件复制一份并改名，养成好习惯
cp tracker.conf.sample tracker.conf 
#修改配置文件
vim tracker.conf
#修改如下几处内容


#基础目录（Tracker运行时会向此目录存储storage的管理数据）
base_path=/fastdfs/tracker
#保存并退出
#创建目录
mkdir -p /fastdfs/tracker

```

## 3.2启动tracker

```shell
#进入启动命令的目录
cd /etc/init.d
#因为启动之前修改过启动目录，我们仍需要再次修改启动命令
vim fdfs_trackerd
#修改如下位置的内容
PRG=/usr/local/bin/fdfs_trackerd

#启动
./fdfs_trackerd start
#启动成功后可以查看一下启动状态是否启动成功

[root@VM-0-12-centos init.d]# ./fdfs_trackerd status
fdfs_trackerd (pid 21341) 正在运行...
#停止启动命令
./fdfs_trackerd stop
#重启命令
./fdfs_trackerd restart

#设置开机自启动（这是一个可选操作，根据自己喜好）
vim /etc/rc.d/rc.local
#在开机自启动的配置文件中添加如下命令
/etc/init.d/fdfs_trackerd start

```

# 四、配置storage

```shell
#进入目录
cd /etc/fdfs
#配置之前先将源文件复制一份并改名，养成好习惯
cp storage.conf.sample storage.conf
#修改配置文件
vim storage.conf
#修改如下几处内容
###################################################
#存放数据和日志的目录地址
base_path=/fastdfs/storage/base
#真正存放文件的目录，storage启动之后会成功256*256个目录用于存储文件
store_path0=/fastdfs/storage/store
#配置tracker地址，如果有多个，在单机环境下我们可以只保留一个
tracker_server=172.81.205.117:22122
#保存并退出
退出之后要去建之前配置的目录
[root@VM-0-12-centos fdfs]# mkdir -p /fastdfs/storage/base
[root@VM-0-12-centos fdfs]# mkdir -p /fastdfs/storage/store

```

## 4.2启动storage

```shell
#进入启动命令的目录
cd /etc/init.d
#因为启动之前修改过启动目录，我们仍需要再次修改启动命令
vim fdfs_storaged
#修改如下位置的内容
PRG=/usr/local/bin/fdfs_storaged
#启动
./fdfs_storaged start
#查看状态
[root@VM-0-12-centos init.d]# ./fdfs_storaged status
fdfs_storaged (pid 28822) 正在运行...

```

## 4.3参观一下数据存放目录

```shell\
cd /fastdfs/storage/store/data
ls

```

可以看到以16进制命名的目录共256个，每个目录中还包含256个子目录

![image-20220226124135035](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20220226124135035.png)

```shell
查看服务
netstat -ntlp
```

# 五、配置客户端（可选）

如果是使用纯java代码操作的话，这一步可以忽略

```shell
#进入配置目录
cd /etc/fdfs

[root@VM-0-12-centos data]# cd /etc/fdfs        
[root@VM-0-12-centos fdfs]# ll
总用量 36
-rw-r--r-- 1 root root 1461 2月  26 11:44 client.conf.sample
-rw-r--r-- 1 root root 7898 2月  26 12:26 storage.conf
-rw-r--r-- 1 root root 7829 2月  26 11:44 storage.conf.sample
-rw-r--r-- 1 root root 7177 2月  26 12:00 tracker.conf
-rw-r--r-- 1 root root 7102 2月  26 11:44 tracker.conf.sample
#还是老样子，先复制文件再去修改，养成好习惯
cp client.conf.sample client.conf
#修改配置文件
vim client.conf
#修改如下配置
base_path=/fastdfs/client
tracker_server=172.81.205.117:22122
#保存并退出，创建所需目录
mkdir -p /fastdfs/client
```

## 5.2上传文件

```shell
#进入目录
cd /usr/local/bin/
#如下就是所有tracher关于文件操作所能用得上的命令
[root@VM-0-12-centos fdfs]# cd /usr/local/bin/
[root@VM-0-12-centos bin]# ll
总用量 24164
-rwxr-xr-x 1 root root  321720 2月  26 11:44 fdfs_appender_test
-rwxr-xr-x 1 root root  321496 2月  26 11:44 fdfs_appender_test1
-rwxr-xr-x 1 root root  308376 2月  26 11:44 fdfs_append_file
-rwxr-xr-x 1 root root  307808 2月  26 11:44 fdfs_crc32
-rwxr-xr-x 1 root root  308400 2月  26 11:44 fdfs_delete_file
-rwxr-xr-x 1 root root  309168 2月  26 11:44 fdfs_download_file
-rwxr-xr-x 1 root root  308744 2月  26 11:44 fdfs_file_info
-rwxr-xr-x 1 root root  322544 2月  26 11:44 fdfs_monitor
-rwxr-xr-x 1 root root 1125160 2月  26 11:44 fdfs_storaged
-rwxr-xr-x 1 root root  331624 2月  26 11:44 fdfs_test
-rwxr-xr-x 1 root root  326736 2月  26 11:44 fdfs_test1
-rwxr-xr-x 1 root root  465168 2月  26 11:44 fdfs_trackerd
-rwxr-xr-x 1 root root  309360 2月  26 11:44 fdfs_upload_appender
-rwxr-xr-x 1 root root  310384 2月  26 11:44 fdfs_upload_file
-rwxr-xr-x 1 root root    1768 2月  26 11:44 restart.sh
-rwxr-xr-x 1 root root    1680 2月  26 11:44 stop.sh

```

我们使用文件上传的命令`fdfs_upload_file`

```shell
#随便挑一个你主机里面的文件，我就把这个命令事务文件给上传了.
 ./fdfs_upload_file /etc/fdfs/client.conf ./fdfs_upload_file
#上传成功之后就会返回卷名加文件名
group1/M00/00/00/rBEADGIZs4OAZzTPAAS8cObeCbQ1309199
#可以根据文件路径找到，上传的位置
```

深入解析返回路径group1/M00/00/00/rBEADGIZs4OAZzTPAAS8cObeCbQ1309199

1. group1就是storage配置的卷名，使用命令 cat /etc/fdfs/storage.conf查看我们以前配置过的卷名group_name=group1
2. M00虚拟目录，暂时不用管
3. /00/00/文件保存的路径
4. rBEADGIZs4OAZzTPAAS8cObeCbQ1309199文件经过重名名后的名称

- 进入storage目录查看刚刚上传的文件

```shell
[root@VM-0-12-centos 00]# cd /fastdfs/storage/store/
[root@VM-0-12-centos store]# ls
data
[root@VM-0-12-centos store]# cd data/00/00
[root@VM-0-12-centos 00]# ls
rBEADGIZs4OAZzTPAAS8cObeCbQ1309199
#这就是我们刚刚上传的文件
```

5.3删除文件

删除文件需要完整的卷名加文件名

```shell
cd /usr/local/bin/

./fdfs_delete_file /etc/fdfs/client.conf group1/M00/00/00/rBEADGIZs4OAZzTPAAS8cObeCbQ1309199 
#再次查看文件存储位置，发现文件消失，就说明文件被删除了
[root@VM-0-12-centos bin]# cd /fastdfs/storage/store/data/00/00                                                                                                                                                                                       
[root@VM-0-12-centos 00]# ll
总用量 0
[root@VM-0-12-centos 00]# 

```

# 六、安装Nginx-组件模块

```shell
cd /usr/local/fastdfs/
#解压Nginx压缩包
 tar -zxvf fastdfs-nginx-module-1.22.tar.gz
 #进入src目录修改相关配置
 cd fastdfs-nginx-module-1.22/src
 
[root@VM-0-12-centos src]# ll
总用量 84
-rw-rw-r-- 1 root root 43501 7月   5 2018 common.c
-rw-rw-r-- 1 root root  3995 7月   5 2018 common.h
-rw-rw-r-- 1 root root   848 7月   5 2018 config
-rw-rw-r-- 1 root root  3725 7月   5 2018 mod_fastdfs.conf
-rw-rw-r-- 1 root root 28668 7月   5 2018 ngx_http_fastdfs_module.c
#修改config文件
vim config
#修改以下内容
CORE_INCS="$CORE_INCS /usr/local/include/fastdfs /usr/include/fastcommon"
#保存并退出
```

## 6.1安装nginx

安装依赖

```shell
yum install -y gcc gcc-c++ make automake autoconf libtool pcre pcre-develzlib zlib-devel openssl openssl-devel 
```

进入cd /usr/local/fastdfs/目录

用`tar -zxvf`命令解压nginx的tar包

```shell
#创建目录备用
mkdir -p /var/temp/nginx

```

配置nginx安装信息

```shell
cd /usr/local/fastdfs/nginx-1.16.1/


./configure \
--prefix=/usr/local/nginx \
--pid-path=/var/run/nginx/nginx.pid \
--lock-path=/var/lock/nginx.lock \
--error-log-path=/var/log/nginx/error.log \
--http-log-path=/var/log/nginx/access.log \
--with-http_gzip_static_module \
--http-client-body-temp-path=/var/temp/nginx/client \
--http-proxy-temp-path=/var/temp/nginx/proxy \
--http-fastcgi-temp-path=/var/temp/nginx/fastcgi \
--http-uwsgi-temp-path=/var/temp/nginx/uwsgi \
--http-scgi-temp-path=/var/temp/nginx/scgi \
--add-module=/usr/local/fastdfs/fastdfs-nginx-module-1.22/src
```

**-add-module必须定义，此配置信息是用于指定安装Nginx时需要加载的模块，如果未能指定，nginx安装过程中就不会加载fastdfs-nginx-module模块，后续功能无法实现。**  后面的路径就是解压fastdfs-nginx-module产生的绝对路径

安装

```shell
cd /usr/local/fastdfs/nginx-1.16.1/
make
make install
修改配置文件
cd /usr/local/fastdfs/fastdfs-nginx-module-1.22/src
 cp mod_fastdfs.conf /etc/fdfs/
 
 tracker_server=172.16.96.128:22122
 url_have_group_name = true
 store_path0=/fastdfs/storage/store
 
 [root@localhost fdfs]# cp /usr/local/fastdfs/fastdfs-6.06/conf/http.conf /etc/fdfs/
[root@localhost fdfs]# cp /usr/local/fastdfs/fastdfs-6.06/conf/mime.types /etc/fdfs/

创建nginx启动软链接
ln -s /usr/local/lib64/libfdfsclient.so /usr/lib64/libfdfsclient.so

网络追踪软链接
ln -s /fastdfs/storage/store/data/ /fastdfs/storage/store/data/M00

修改nginx配置
cd /usr/local/fastdfs/nginx-1.16.1/conf
vim nginx.conf

user  root;
listen       8888;
端口8888的原因storage.conf里面配置的就是8888端口
```

```

user  root;
worker_processes  1;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    #keepalive_timeout  0;
    keepalive_timeout  65;

    #gzip  on;

    server {
        listen       8888;
        server_name  localhost;
        location ~/group[0-9]/M00{
            ngx_fastdfs_module;
        }

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            root   html;
            index  index.html index.htm;
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

        # proxy the PHP scripts to Apache listening on 127.0.0.1:80
        #
        #location ~ \.php$ {
        #    proxy_pass   http://127.0.0.1;
        #}

        # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
        #
        #location ~ \.php$ {
        #    root           html;
        #    fastcgi_pass   127.0.0.1:9000;
        #    fastcgi_index  index.php;
        #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
        #    include        fastcgi_params;
        #}

        # deny access to .htaccess files, if Apache's document root
        # concurs with nginx's one
        #
        #location ~ /\.ht {
        #    deny  all;
        #}
    }


    # another virtual host using mix of IP-, name-, and port-based configuration
    #
    #server {
    #    listen       8000;
    #    listen       somename:8080;
    #    server_name  somename  alias  another.alias;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}


    # HTTPS server
    #
    #server {
    #    listen       443 ssl;
    #    server_name  localhost;

    #    ssl_certificate      cert.pem;
    #    ssl_certificate_key  cert.key;

    #    ssl_session_cache    shared:SSL:1m;
    #    ssl_session_timeout  5m;

    #    ssl_ciphers  HIGH:!aNULL:!MD5;
    #    ssl_prefer_server_ciphers  on;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}

}

```

重启storage

启动nginx

/usr/local/nginx/sbin



