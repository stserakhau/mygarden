package com.i4biz.mygarden.service;


import com.i4biz.mygarden.domain.SpeciesTaskFertilizer;
import com.i4biz.mygarden.domain.SpeciesTaskFertilizerView;

public interface ISpeciesTaskFertilizerService extends IService<SpeciesTaskFertilizerView, SpeciesTaskFertilizer, Long> {

    void deleteSpeciesTask(long speciesId, long speciesTaskId);

    SpeciesTaskForm loadForm(long speciesTaskId);

    long saveForm(SpeciesTaskForm form);

    SpeciesTaskFertilizer findOrCreate(SpeciesTaskFertilizer speciesTask);

    SpeciesTaskFertilizer create(SpeciesTaskFertilizer speciesTask);

    void delete(long fertilizerId, long speciesTaskId);

    public static class SpeciesTaskForm {
        public SpeciesTaskFertilizer speciesTask;

        public String taskName;

        public SpeciesTaskForm() {
        }

        public SpeciesTaskForm(SpeciesTaskFertilizer speciesTask, String taskName) {
            this.speciesTask = speciesTask;
            this.taskName = taskName;
        }

        public SpeciesTaskFertilizer getSpeciesTask() {
            return speciesTask;
        }

        public void setSpeciesTask(SpeciesTaskFertilizer speciesTask) {
            this.speciesTask = speciesTask;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }
    }
}
