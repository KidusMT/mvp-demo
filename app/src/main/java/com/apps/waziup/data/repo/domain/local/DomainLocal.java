package com.apps.waziup.data.repo.domain.local;

import com.apps.waziup.data.model.Domain;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.reactivex.Observable;

/**
 * Created by KidusMT on 4/4/2018.
 */

public class DomainLocal implements DomainLocalContract {

    private Box<Domain> box;

    public DomainLocal(BoxStore store) {
        box = store.boxFor(Domain.class);
    }

    @Override
    public Observable<Boolean> saveDomain(List<Domain> domains) {
        for (int i = 0; i < domains.size(); i++) {
            Domain newDomain = domains.get(i);
            Domain found = box.query().build().findFirst();
            if (found == null) {
                box.put(newDomain);
            } else {
                newDomain._id = found._id;
                box.put(newDomain);
            }
        }

        return Observable.just(true);
    }

    @Override
    public Observable<List<Domain>> getDomains() {
        List<Domain> domains = box.getAll();
        return Observable.just(domains);
    }

    @Override
    public Observable<Boolean> deleteDomain(Domain domain) {
        return null;
    }

    @Override
    public Observable<Boolean> updateDomain(Domain domain) {
        return null;
    }

    @Override
    public int size() {
        return box.getAll().size();
    }
}
