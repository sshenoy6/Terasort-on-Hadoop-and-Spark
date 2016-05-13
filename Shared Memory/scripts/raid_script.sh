sudo su
apt-get install mdadm
mdadm --create /dev/md0 --run --level=0 --name=MY_RAID --raid-devices=2 /dev/xvdb /dev/xvdc
mkfs.ext4 -L MY_RAID /dev/md0
mkdir -p /mnt/raid
mount LABEL=MY_RAID /mnt/raid
chmod 777 /mnt/raid
