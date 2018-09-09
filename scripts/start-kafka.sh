#!/bin/bash

KAFKA=/Users/maxbaylis/msc-project/kafka_2.11-1.1.0
TOPIC=blocks
TOPIC2=pool-tx


tmux kill-session -t kafka-start
tmux new-session -d -s "kafka-start"
#tmux rename-window -t 1 kafka-start

# Setup a panes to run the zookeeper and kafka servers
tmux split-window -h
tmux split-window -v
tmux split-window -v
tmux select-pane -t 0
tmux split-window -v

# Start zookeeper
tmux select-pane -t 3
tmux send-keys "$KAFKA/bin/zookeeper-server-start.sh $KAFKA/config/zookeeper.properties" C-m

# Start kafka server
tmux select-pane -t 4
tmux send-keys "$KAFKA/bin/kafka-server-start.sh $KAFKA/config/server.properties" C-m

# Create topics and verify
tmux select-pane -t 2
tmux send-keys "$KAFKA/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic $TOPIC" C-m
tmux send-keys "$KAFKA/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic $TOPIC2" C-m

tmux send-keys "$KAFKA/bin/kafka-topics.sh --list --zookeeper localhost:2181" C-m


# Create a command line consumer
tmux select-pane -t 1
tmux send-keys "$KAFKA/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic $TOPIC --from-beginning" C-m
echo "Topic $TOPIC messages should appear hear"

# Create a command line producer
tmux select-pane -t 0
# tmux send-keys "$KAFKA/bin/kafka-verifiable-producer.sh --topic $TOPIC --max-messages 200000 --broker-list localhost:9092" C-m
tmux send-keys "$KAFKA/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic $TOPIC" C-m
echo "Type $TOPIC messages here"


tmux attach-session -t kafka-start



#$KAFKA/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3  --config retention.ms=10000 --topic $TOPIC

# :)
#$KAFKA/bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --alter --add-config retention.ms=60 --entity-name blocks


# $KAFKA/bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic pool-tx --config retention.ms=10000
# $KAFKA/bin/kafka-topics.sh --zookeeper localhost:2181 --entity-type topic --entity-name pool-tx --alter --add-config retention.ms=1000

#$KAFKA/bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topics-with-overrides
#$KAFKA/bin/kafka-topics.sh bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic pool-tx