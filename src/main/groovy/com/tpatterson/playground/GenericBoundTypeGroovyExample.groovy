package com.tpatterson.playground;

/**
 * Decent groovy example
 */

import com.theplatform.data.api.Range
import com.theplatform.data.api.Sort
import com.theplatform.data.api.client.DataService
import com.theplatform.data.api.client.query.ByOwnerId
import com.theplatform.data.api.client.query.Query
import com.theplatform.data.api.objects.DataObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class GenericBoundTypeGroovyExample //ServiceHelper
{
    static final int READ_BATCH_SIZE = 100
    static final int WRITE_BATCH_SIZE = 20

    private static final Logger LOG = LoggerFactory.getLogger(GenericBoundTypeGroovyExample)


    public static <T extends DataObject> long deleteObjects(DataService<T> client, List<T> objects, boolean deleteLocked = false)
    {
        if (!objects)
        {
            return 0;
        }
        LOG.info('Deleting {} {} objects from service {}', objects.size(), objects[0].class.simpleName, client.baseUrl)

        List<List<T>> toDelete = objects.collate(WRITE_BATCH_SIZE)

        long deleted = 0

        toDelete.eachWithIndex { List<T> objsToDelete, int index ->
            LOG.debug(" ... batch {} out of {}", index + 1, toDelete.size())
            if (deleteLocked)
            {
                objsToDelete.each{ o ->
                    o.locked = false
                }
                client.update(objsToDelete)
            }
            deleted += client.delete(objsToDelete.collect {it.id} as URI[])
        }

        return deleted
    }

    public static <T extends DataObject> List<T> getObjects(
        DataService<T> client, String accountId, List<String> fields = [], List<Query> additionalQueries = [])
    {
        List<Query> queries = additionalQueries + [new ByOwnerId(URI.create(accountId))]
        return getObjects(client, fields, queries)
    }

    public static <T extends DataObject> List<T> getObjects(
        DataService<T> client, List<String> fields = [], List<Query> additionalQueries = [])
    {
        Range range = new Range(1, READ_BATCH_SIZE)

        List<T> results = []

        List<T> tmpResults = client.getAll(
            fields as String[], additionalQueries as Query[], [] as Sort[], range, false).entries

        results.addAll(tmpResults)

        while (tmpResults.size() == READ_BATCH_SIZE)
        {
            range.startIndex += READ_BATCH_SIZE
            range.endIndex += READ_BATCH_SIZE
            tmpResults = client.getAll(
                fields as String[], additionalQueries as Query[], null, range, false).entries
            results.addAll(tmpResults)
        }

        return results
    }

    public static <T extends DataObject> List<T> getObjectsById(
        DataService<T> client, List<URI> ids, List<String> fields = [])
    {
        Range range = new Range(1, READ_BATCH_SIZE)

        List<T> results = []

        List<T> tmpResults = client.get(
            ids as URI[], fields as String[], [] as Query[], [] as Sort[], range).entries

        results.addAll(tmpResults)

        while (tmpResults.size() == READ_BATCH_SIZE)
        {
            range.startIndex += READ_BATCH_SIZE
            range.endIndex += READ_BATCH_SIZE
            tmpResults = client.getAll(
                fields as String[], [] as Query[], null, range, false).entries
            results.addAll(tmpResults)
        }

        return results
    }

}
