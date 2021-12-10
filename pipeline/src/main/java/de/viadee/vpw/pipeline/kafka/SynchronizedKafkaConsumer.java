package de.viadee.vpw.pipeline.kafka;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decorator für einen {@link Consumer}, der alle Methodenaufrufe mit einem exklusiven Lock (siehe {@link
 * ReentrantLock}) ausführt und somit Thread-safe ist.
 */
public class SynchronizedKafkaConsumer<K, V> implements Consumer<K, V> {

    private static AtomicInteger counter = new AtomicInteger();

    private final Logger logger = LoggerFactory.getLogger(SynchronizedKafkaConsumer.class);

    private final ReentrantLock lock = new ReentrantLock(true);

    private final Consumer<K, V> consumer;

    private final int id;

    public SynchronizedKafkaConsumer(Consumer<K, V> consumer) {
        this.consumer = Objects.requireNonNull(consumer);
        id = counter.incrementAndGet();
        logger.debug("Created consumer #{}", id);
    }

    @Override
    public Set<TopicPartition> assignment() {
        return executeWithLock(consumer::assignment);
    }

    @Override
    public Set<String> subscription() {
        return executeWithLock(consumer::subscription);
    }

    @Override
    public void subscribe(Collection<String> topics) {
        executeWithLock(() -> consumer.subscribe(topics));
    }

    @Override
    public void subscribe(Collection<String> topics, ConsumerRebalanceListener callback) {
        executeWithLock(() -> consumer.subscribe(topics, callback));
    }

    @Override
    public void assign(Collection<TopicPartition> partitions) {
        executeWithLock(() -> consumer.assign(partitions));
    }

    @Override
    public void subscribe(Pattern pattern, ConsumerRebalanceListener callback) {
        executeWithLock(() -> consumer.subscribe(pattern, callback));
    }

    @Override
    public void subscribe(Pattern pattern) {
        executeWithLock(() -> consumer.subscribe(pattern));
    }

    @Override
    public void unsubscribe() {
        executeWithLock(consumer::unsubscribe);
    }

    @Override
    public ConsumerRecords<K, V> poll(long timeout) {
        return executeWithLock(() -> consumer.poll(timeout));
    }

    @Override
    public ConsumerRecords<K, V> poll(Duration timeout) {
        return executeWithLock(() -> consumer.poll(timeout));
    }

    @Override
    public void commitSync() {
        executeWithLock((Action) consumer::commitSync);
    }

    @Override
    public void commitSync(Duration timeout) {
        executeWithLock(() -> consumer.commitSync(timeout));
    }

    @Override
    public void commitSync(Map<TopicPartition, OffsetAndMetadata> offsets) {
        executeWithLock(() -> consumer.commitSync(offsets));
    }

    @Override
    public void commitSync(Map<TopicPartition, OffsetAndMetadata> offsets, Duration timeout) {
        executeWithLock(() -> consumer.commitSync(offsets, timeout));
    }

    @Override
    public void commitAsync() {
        executeWithLock((Action) consumer::commitAsync);
    }

    @Override
    public void commitAsync(OffsetCommitCallback callback) {
        executeWithLock(() -> consumer.commitAsync(callback));
    }

    @Override
    public void commitAsync(Map<TopicPartition, OffsetAndMetadata> offsets, OffsetCommitCallback callback) {
        executeWithLock(() -> consumer.commitAsync(offsets, callback));
    }

    @Override
    public void seek(TopicPartition partition, long offset) {
        executeWithLock(() -> consumer.seek(partition, offset));
    }

    /* Overrides the fetch offsets that the consumer will use on the next poll(timeout) or
     * in other words changes (overrides) the current offset in the consumer so it will start consuming messages from that in the next poll()
     * By overriding the offset, you can move backwards to previous messages or browse through the history.
     */
    @Override
    public void seek(TopicPartition topicPartition, OffsetAndMetadata offsetAndMetadata) {
        executeWithLock(() -> consumer.seek(topicPartition, offsetAndMetadata));
    }

    @Override
    public void seekToBeginning(Collection<TopicPartition> partitions) {
        executeWithLock(() -> consumer.seekToBeginning(partitions));
    }

    @Override
    public void seekToEnd(Collection<TopicPartition> partitions) {
        executeWithLock(() -> consumer.seekToEnd(partitions));
    }

    @Override
    public long position(TopicPartition partition) {
        return executeWithLock(() -> consumer.position(partition));
    }

    @Override
    public long position(TopicPartition partition, Duration timeout) {
        return executeWithLock(() -> consumer.position(partition, timeout));
    }

    // Deprecated, use Set instead
    @Override
    public OffsetAndMetadata committed(TopicPartition partition) {
        return executeWithLock(() -> consumer.committed(partition));
    }

    // Deprecated, use Set instead
    @Override
    public OffsetAndMetadata committed(TopicPartition partition, Duration timeout) {
        return executeWithLock(() -> consumer.committed(partition, timeout));
    }

    /* TopicPartition now as data structure Set
     * Marking an offset as consumed is called committing an offset.
     * In Kafka, we record offset commits by writing to an internal Kafka topic called the offsets topic.
     * A message is considered consumed only when its offset is committed to the offsets topic.
     */
    @Override
    public Map<TopicPartition, OffsetAndMetadata> committed(Set<TopicPartition> set) {
        return executeWithLock(() -> consumer.committed(set));
    }

    /* TopicPartition now as data structure Set
     * Marking an offset as consumed is called committing an offset.
     * In Kafka, we record offset commits by writing to an internal Kafka topic called the offsets topic.
     * A message is considered consumed only when its offset is committed to the offsets topic.
     */
    @Override
    public Map<TopicPartition, OffsetAndMetadata> committed(Set<TopicPartition> set, Duration duration) {
        return executeWithLock(() -> consumer.committed(set, duration));
    }

    @Override
    public Map<MetricName, ? extends Metric> metrics() {
        return executeWithLock(consumer::metrics);
    }

    @Override
    public List<PartitionInfo> partitionsFor(String topic) {
        return executeWithLock(() -> consumer.partitionsFor(topic));
    }

    @Override
    public List<PartitionInfo> partitionsFor(String topic, Duration timeout) {
        return executeWithLock(() -> consumer.partitionsFor(topic, timeout));
    }

    @Override
    public Map<String, List<PartitionInfo>> listTopics() {
        return executeWithLock(() -> consumer.listTopics());
    }

    @Override
    public Map<String, List<PartitionInfo>> listTopics(Duration timeout) {
        return executeWithLock(() -> consumer.listTopics(timeout));
    }

    @Override
    public Set<TopicPartition> paused() {
        return executeWithLock(consumer::paused);
    }

    @Override
    public void pause(Collection<TopicPartition> partitions) {
        executeWithLock(() -> consumer.pause(partitions));
    }

    @Override
    public void resume(Collection<TopicPartition> partitions) {
        executeWithLock(() -> consumer.resume(partitions));
    }

    @Override
    public Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(Map<TopicPartition, Long> timestampsToSearch) {
        return executeWithLock(() -> consumer.offsetsForTimes(timestampsToSearch));
    }

    @Override
    public Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(Map<TopicPartition, Long> timestampsToSearch,
            Duration timeout) {
        return executeWithLock(() -> consumer.offsetsForTimes(timestampsToSearch, timeout));
    }

    @Override
    public Map<TopicPartition, Long> beginningOffsets(Collection<TopicPartition> partitions) {
        return executeWithLock(() -> consumer.beginningOffsets(partitions));
    }

    @Override
    public Map<TopicPartition, Long> beginningOffsets(Collection<TopicPartition> partitions, Duration timeout) {
        return executeWithLock(() -> consumer.beginningOffsets(partitions, timeout));
    }

    @Override
    public Map<TopicPartition, Long> endOffsets(Collection<TopicPartition> partitions) {
        return executeWithLock(() -> consumer.endOffsets(partitions));
    }

    @Override
    public Map<TopicPartition, Long> endOffsets(Collection<TopicPartition> partitions, Duration timeout) {
        return executeWithLock(() -> consumer.endOffsets(partitions, timeout));
    }

    // Return the current group metadata associated with this consumer.
    @Override
    public ConsumerGroupMetadata groupMetadata() {
       return  consumer.groupMetadata();
    }

    /* You do not need to call this during normal processing, as the consumer group will manage itself automatically and rebalance when necessary.
     * However there may be situations where the application wishes to trigger a rebalance that would otherwise not occur.
     * For example, if some condition external and invisible to the Consumer and its group changes in a way that would affect the userdata encoded in the Subscription, the Consumer will not be notified and no rebalance will occur.
     * This API can be used to force the group to rebalance so that the assignor can perform a partition reassignment based on the latest userdata.
     * If your assignor does not use this userdata, or you do not use a custom ConsumerPartitionAssignor, you should not use this API.
     */
    @Override
    public void enforceRebalance() {
        consumer.enforceRebalance();
    }

    @Override
    public void close() {
        executeWithLock((Action) consumer::close);
    }

    @Override
    public void close(long timeout, TimeUnit unit) {
        executeWithLock(() -> consumer.close(timeout, unit));
    }

    @Override
    public void close(Duration timeout) {
        executeWithLock(() -> consumer.close(timeout));
    }

    @Override
    public void wakeup() {
        consumer.wakeup();
    }

    private <T> T executeWithLock(Supplier<T> supplier) {
        lock();
        try {
            return supplier.get();
        } finally {
            unlock();
        }
    }

    private void executeWithLock(Action action) {
        lock();
        try {
            action.execute();
        } finally {
            unlock();
        }
    }

    private void lock() {
        logger.trace("Consumer #{}: Acquire lock for method '{}' by thread '{}' | hasQueued={} | isLocked={}", id,
                getMethodName(), getThreadName(), lock.hasQueuedThreads(), lock.isLocked());
        lock.lock();
    }

    private void unlock() {
        try {
            logger.trace("Consumer #{}: Release lock for method '{}' by thread '{}'", id, getMethodName(),
                    getThreadName());
        } finally {
            lock.unlock();
        }
    }

    private String getMethodName() {
        return Thread.currentThread().getStackTrace()[4].getMethodName();
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }

    @FunctionalInterface
    private interface Action {

        void execute();
    }
}
