FROM fup1990/easyid:latest
#-zookeeper:zookeeper地址，-redis:ridis地址
CMD ["java","-jar","/EasyID-Server-STABLE-1.0.jar","-zookeeper192.168.56.102:2181","-redis192.168.56.102:6379"]
