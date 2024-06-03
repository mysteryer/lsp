import org.apache.spark.sql.types._
import org.apache.spark.sql.streaming.{OutputMode, StreamingQuery}
import org.apache.spark.sql.{SparkSession, DataFrame, ForeachWriter, Row}
import spark.implicits._
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory, Put, Table}
import org.apache.hadoop.hbase.security.User
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.util.Bytes
import scala.util.{Failure, Success, Try}


class HBaseShipmentWriter extends ForeachWriter[Row] {
  private val tableName = "qxdc-device"
  private var connection: Connection = _
  private var hTable: Table = _
  override def open(partitionId: Long, epochId: Long): Boolean = {
    val conf = new HBaseConfiguration()
    connection = ConnectionFactory.createConnection(conf);
    hTable = connection.getTable(TableName.valueOf(tableName))
    true
  }

  override def process(value: Row): Unit = {
    
    val put = new Put(value.getAs[String]("rowkey").getBytes())
    put.addColumn(Bytes.toBytes("device_object"), value.getAs[String]("deviceObjectCode").getBytes(), value.getAs[String]("stringValue").getBytes())
    hTable.put(put)
  }

  override def close(errorOrNull: Throwable): Unit = {
    
    hTable.close()
    connection.close()
  }
}

val kafkaBootstrapServers = "hadoop001:9092,hadoop002:9092,hadoop003:9092"
val inputDF = spark.readStream.format("kafka").option("kafka.bootstrap.servers", kafkaBootstrapServers).option("subscribe","devices-qxdc-topic").load()
val outputDF = inputDF.selectExpr("CAST(key AS STRING) AS key", "CAST(value AS STRING) as value", "CAST(timestamp as timestamp) as date ")
val jsonSchema = new StructType().add("deviceCode", StringType, nullable = true).add("objectCode", StringType, nullable = true).add("value", StringType, nullable = true)
val query1 = outputDF.select(from_json($"value", jsonSchema).as("data"),$"date").selectExpr("data.deviceCode as deviceCode","data.objectCode as deviceObjectCode","data.value as stringValue", "date")
val query = query1.withColumn("date",unix_timestamp($"date","yyyy-MM-dd HH:mm:ss")+(-2147483647)).withColumn("rowkey",concat($"deviceCode", $"date")).select($"rowkey", $"deviceObjectCode", $"stringValue")

query.writeStream.foreach(new HBaseShipmentWriter()).start().awaitTermination()

query1.writeStream.format("console").start().awaitTermination()





import org.apache.hadoop.hbase.security.User
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.spark.sql.{Dataset, Row}
import org.apache.spark.sql.streaming.{GroupState, OutputMode, StreamingQuery, StreamingQueryException}
import org.apache.spark.sql.functions._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.types._
import org.apache.spark.sql.streaming.{OutputMode, StreamingQuery}
import spark.implicits._



val hbaseConf = HBaseConfiguration.create()
val connection: Connection = ConnectionFactory.createConnection(hbaseConf)


val kafkaBootstrapServers = "hadoop001:9092,hadoop002:9092,hadoop003:9092"
val inputDF = spark.readStream.format("kafka").option("kafka.bootstrap.servers", kafkaBootstrapServers).option("subscribe","devices-topic").load()
val outputDF = inputDF.selectExpr("CAST(key AS STRING) AS key", "CAST(value AS STRING) as value", "CAST(timestamp as timestamp) as date ")
val jsonSchema = new StructType().add("deviceCode", StringType, nullable = true).add("deviceName", StringType, nullable = true).add("deviceObjectCode", StringType, nullable = true).add("deviceObjectName", StringType, nullable = true).add("stringValue", StringType, nullable = true)
val query1 = outputDF.select(from_json($"value", jsonSchema).as("data"),$"date").selectExpr("data.deviceCode as deviceCode","data.deviceObjectCode as deviceObjectCode","data.stringValue as stringValue", "date")
val query = query1.withColumn("date",unix_timestamp($"date","yyyy-MM-dd HH:mm:ss")+(-2147483647)).withColumn("rowkey",concat($"deviceCode", $"date")).select($"rowkey", $"deviceObjectCode", $"stringValue")

query.writeStream.foreachBatch { (batchDF: Dataset[Row], batchId: Long) =>
    
  val tableName = TableName.valueOf("idec_device")
  val table: Table = connection.getTable(tableName)
  
  batchDF.foreachPartition { partition: Iterator[Row] =>
    
    val puts = partition.map { row =>

      val id = row.getAs[String]("rowkey")
      val put = new Put(id.getBytes())
      
      // 添加列到Put对象，这里需要根据你的HBase表结构进行调整
      put.addColumn(Bytes.toBytes("device_object"), row.getAs[String]("deviceObjectCode").getBytes(), row.getAs[String]("stringValue").getBytes())
    (id, put)
    }

    val buffer = puts.buffered

    try {
      while (buffer.hasNext) {
        val (id, put) = buffer.next()
        table.put(put)
      }
    } catch {
      case e: Exception =>
        // 处理异常，可以选择重新抛出或者记录日志
        throw new StreamingQueryException(s"Error inserting data to HBase: ${e.getMessage}")
    }
    
    // 关闭资源
    buffer.close()
  }

  // 关闭表连接
  table.close()
}.outputMode(OutputMode.Append()).start()

// 关闭HBase连接
connection.close()






2D-1F-P07-3GH-APM-02_1715669491 XSYGDNECC 正常

import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory, Put, Table}
import org.apache.hadoop.hbase.security.User
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.util.Bytes

val hbaseConf = HBaseConfiguration.create()
val connection: Connection = ConnectionFactory.createConnection(hbaseConf)
val tableName = TableName.valueOf("idec_device")
val table: Table = connection.getTable(tableName)

val put = new Put(Bytes.toBytes("2D-1F-P07-3GH-APM-02_1715669491"))
put.addColumn(Bytes.toBytes("device_object"), Bytes.toBytes("XSYGDNECC"), Bytes.toBytes("正常"))
table.put(put)



import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory, Put, Table}
import org.apache.hadoop.hbase.security.User
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.util.Bytes

val hbaseConf = HBaseConfiguration.create()
val connection: Connection = ConnectionFactory.createConnection(hbaseConf)
val tableName = TableName.valueOf("idec_device")
val table: Table = connection.getTable(tableName)

import import org.apache.spark.sql.{Dataset, Row}

val testDf = 

CREATE EXTERNAL TABLE default.lrtd_wd_df(key string, value1 string) STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES (
"hbase.columns.mapping" = ":key,device_object:WD"
)TBLPROPERTIES(
"hbase.table.name"="LRTD_Device",
"hbase.mapred.output.outputtable"="LRTD_Device"
);


create table if not exists default.device_df(
  id bigint,
  code varchar(64),
  `model_code` varchar(64),
  `slave_id` bigint,
  `pi_id` bigint,
  `camera_config_id` bigint,
  `category` varchar(2),
  `type` varchar(4) ,
  `name` varchar(50),
  `floor` varchar(50),
  `room` varchar(50),
  `location` bigint,
  `location_ids` varchar(255),
  `location_name` varchar(255),
  `location_detail` varchar(255),
  `offline_alarm_flag` varchar(2),
  `offline_alarm_level` varchar(2),
  `offline_alarm_content` varchar(500),
  `fast_offline_num` int,
  `type_tag` varchar(64),
  `alarm_duration` int,
  `traffic_status` char(1),
  `alarm_count` int,
  `use_ni` tinyint,
  `read_interval` bigint,
  `create_by` varchar(100),
  `create_date` timestamp ,
  `update_by` varchar(100),
  `update_date` timestamp ,
  `version` bigint,
  `state` char(1),
  `remarks` varchar(100))
COMMENT '2号楼设备表'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
STORED AS TEXTFILE;




SET 'execution.checkpointing.interval' = '3s';

CREATE TABLE products (
    id INT,
    name STRING,
    description STRING,
    PRIMARY KEY (id) NOT ENFORCED
  ) WITH (
    'connector' = 'tidb-cdc',
    'tikv.grpc.timeout_in_ms' = '20000',
    'pd-addresses' = '10.7.248.83:2379',
    'database-name' = 'test',
    'table-name' = 'products'
  );


CREATE TABLE product_view_kafka_sink(
`id` int,
`name` string,
`description` STRING,
PRIMARY KEY (`id`) NOT ENFORCED
) WITH (
 'connector' = 'upsert-kafka',
 'topic' = 'flink-cdc-kafka-test',
 'properties.bootstrap.servers' = 'hadoop001:9092,hadoop002:9092,hadoop003:9092',
 'properties.group.id' = 'flink-cdc-kafka-group',
 'key.format' = 'json',
 'value.format' = 'json'
);
insert into product_view_kafka_sink select * from products;


import org.apache.spark.sql.types._
import org.apache.spark.sql.streaming.{OutputMode, StreamingQuery}
import org.apache.spark.sql.{SparkSession, DataFrame, ForeachWriter, Row}
import spark.implicits._
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory, Put, Table}
import org.apache.hadoop.hbase.security.User
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.util.Bytes
import scala.util.{Failure, Success, Try}
import  scala.collection.JavaConverters._


val kafkaBootstrapServers = "hadoop001:9092,hadoop002:9092,hadoop003:9092"
val inputDF = spark.readStream.format("kafka").option("kafka.bootstrap.servers", kafkaBootstrapServers).option("subscribe","devices-qxdc-topic").load()
val outputDF = inputDF.selectExpr("CAST(key AS STRING) AS key", "CAST(value AS STRING) as value", "CAST(timestamp as timestamp) as date ")
val jsonSchema = new StructType().add("deviceCode", StringType, nullable = true).add("objectCode", StringType, nullable = true).add("value", StringType, nullable = true)
val query1 = outputDF.select(from_json($"value", jsonSchema).as("data"),$"date").selectExpr("data.deviceCode as deviceCode","data.objectCode as deviceObjectCode","data.value as stringValue", "date")
val query = query1.withColumn("date",unix_timestamp($"date","yyyy-MM-dd HH:mm:ss")+(-2147483647)).withColumn("rowkey",concat($"deviceCode", $"date")).select($"rowkey", $"deviceObjectCode", $"stringValue")

def writeFunction: (DataFrame, Long) => Unit = {
    (batchData, id) => {
      batchData.foreachPartition(
        (hbasePartition:Iterator[Row]) => {  
          val config = new HBaseConfiguration()
          val tableName = TableName.valueOf("qxdc-device")
          val connection: Connection = ConnectionFactory.createConnection(config)
          val table = connection.getTable(tableName)
          val putList = new java.util.LinkedList[Put]()
          hbasePartition.map(value => {
            val put = new Put(value.getAs[String]("rowkey").getBytes())
            put.addImmutable(Bytes.toBytes("device_object"), value.getAs[String]("deviceObjectCode").getBytes(), value.getAs[String]("stringValue").getBytes())
            putList.add(put)
          })
        table.put(putList)
        table.close()
        connection.close()
        }
      )
    }
  }
query.writeStream.foreachBatch(writeFunction).start().awaitTermination()