package com.travelex.tgbp.rules.dynamic.capture;

import java.util.List;

import com.travelex.tgbp.store.domain.PersistentEntity;
import com.travelex.tgbp.store.service.api.DataStore;
import com.travelex.tgbp.store.service.api.Query;

public class DummyDataStore implements DataStore {

    public <T> T store(PersistentEntity<T> entity) {
        System.out.println("Storing");
        return entity.getKey();
    }

    public <T extends PersistentEntity<?>> List<T> execute(Query query, Object... params)
    {
        return null;
    }
}
