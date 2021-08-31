package com.i4biz.mygarden.service;


import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.catalog.ItemView;
import lombok.Data;

import java.util.Collection;
import java.util.List;

public interface ISpeciesService extends IService<ItemView, Item, Long> {

    Collection<Item> findAvailableSpeciesLinkedWithFertilizer(long fertilizerId, Long ownerId);

    Item createSpecies(SpeciesForm speciesForm);

    void deleteSpecies(long speciesId, long ownerId);

    long copySystemSpeciesToMy(long systemSpeciesId, ISpeciesService.SpeciesForm speciesForm);

    List<Item> findSpeciesUsedInTask(long taskId, Long userId);

    @Data
    public static class SpeciesForm {
        public Catalog speciesCatalog;
        public Item species;
        public boolean overwriteSpeciesIfExist;

        public SpeciesForm() {
        }

        public SpeciesForm(Catalog speciesCatalog, Item species) {
            this.speciesCatalog = speciesCatalog;
            this.species = species;
        }
    }
}
