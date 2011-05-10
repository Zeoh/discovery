package com.proofpoint.discovery;

import com.proofpoint.node.NodeInfo;
import org.apache.cassandra.config.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import static org.testng.Assert.assertTrue;

public class TestStaticAndDynamicStores
{
    private final static AtomicLong counter = new AtomicLong(0);

    @Test
    public void testBothInitializeProperly()
    {
        CassandraStoreConfig storeConfig = new CassandraStoreConfig()
                .setKeyspace("test_static_and_dynamic_stores" + counter.incrementAndGet());

        NodeInfo nodeInfo = new NodeInfo("testing");
        CassandraServerInfo serverInfo = CassandraServerSetup.getServerInfo();

        CassandraStaticStore staticStore = new CassandraStaticStore(storeConfig, serverInfo, nodeInfo);
        CassandraDynamicStore dynamicStore = new CassandraDynamicStore(storeConfig, serverInfo, new DiscoveryConfig(), nodeInfo, new TestingTimeProvider());
        dynamicStore.initialize();

        assertTrue(staticStore.getAll().isEmpty());
        assertTrue(dynamicStore.getAll().isEmpty());
    }

    @BeforeSuite
    public void setupCassandra()
            throws IOException, TTransportException, ConfigurationException, InterruptedException
    {
        CassandraServerSetup.tryInitialize();
    }

    @AfterSuite
    public void teardownCassandra()
            throws IOException
    {
        CassandraServerSetup.tryShutdown();
    }
}
