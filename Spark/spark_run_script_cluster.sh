./spark-ec2 -k spark -i /home/ubuntu/spark.pem -s 16 -t c3.large --ebs-vol-size=1000 --ami=ami-877142ed -r us-east-1 --spot-price=0.019 launch spark_cluster_16
