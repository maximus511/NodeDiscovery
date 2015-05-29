#!/bin/bash


# Netid
netid=rkn130030

#
# Root directory of project
PROJDIR=$HOME/AOS/Project1

#
# Path to config file
#
CONFIG=$PROJDIR/config.txt

#
# Main project class
#
PROG=NodeDiscoveryServer

node=0

cat $CONFIG | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read i
    echo $i
    while read line || [ -n "$line" ]
    do
        echo $line
		    host=$( echo $line | awk '{ print $2 }' )
        ssh -o StrictHostKeyChecking=no -l "$netid" "$host" "cd $PROJDIR;java $PROG $node" &
        node=$(( node + 1 ))
    done
)