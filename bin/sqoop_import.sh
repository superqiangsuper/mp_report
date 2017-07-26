#!/bin/bash

tableName="mp_group_item"
gid=$1
sql="select * from ${tableName} where gid=${gid} and \$CONDITIONS"

hdfs dfs -rm -f -R  /data_source/mp/$tableName/mp_gid=$gid

sqoop import --driver com.mysql.jdbc.Driver --connect jdbc:mysql://wallet-prod-hadoop-21:3337/marketing-platform --username wallet_river_r --password "wallet_r&UIOLM69o"  --append -m 4 --split-by id  --query "${sql}"  --outdir /home/zhangzhiqiang/java_dir   --target-dir /data_source/mp/$tableName/mp_gid=$gid  --fields-terminated-by '\001' --lines-terminated-by '\n'  --null-string '\\N' --null-non-string '\\N'

hive -e "alter table mp.mpmvn_group_item add if not exists partition(mp_gid=$gid)"