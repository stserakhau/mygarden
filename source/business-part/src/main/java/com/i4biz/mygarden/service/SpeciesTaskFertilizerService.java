package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.ISpeciesTaskFertilizerDAO;
import com.i4biz.mygarden.domain.SpeciesTaskFertilizer;
import com.i4biz.mygarden.domain.SpeciesTaskFertilizerView;
import com.i4biz.mygarden.domain.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpeciesTaskFertilizerService extends AbstractService<SpeciesTaskFertilizerView, SpeciesTaskFertilizer, Long> implements ISpeciesTaskFertilizerService {
    @Autowired
    private ISpeciesTaskFertilizerDAO speciesTaskFertilizerDAO;

    @Autowired
    private ITaskService taskService;

    @Override
    protected GenericDAO<SpeciesTaskFertilizerView, SpeciesTaskFertilizer, Long> getServiceDAO() {
        return speciesTaskFertilizerDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSpeciesTask(long speciesId, long speciesTaskId) {
        SpeciesTaskFertilizer st = speciesTaskFertilizerDAO.findById(speciesTaskId);
        if (speciesId != st.getSpeciesId()) {
            throw new RuntimeException("Constraint exception on removing. speciesId=" + speciesId + " speciesTaskId=" + speciesTaskId);
        }

        speciesTaskFertilizerDAO.delete(st);
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public SpeciesTaskForm loadForm(long speciesTaskId) {
        SpeciesTaskFertilizer st = speciesTaskFertilizerDAO.findById(speciesTaskId);
        if (st == null) {
            return null;
        }
        Task t = taskService.findById(st.getTaskId());
        return new SpeciesTaskForm(
                st,
                t.getName()
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public long saveForm(SpeciesTaskForm form) {
        String taskName = form.getTaskName();

        Task task = taskService.findOrCreateTaskByName(taskName);

        SpeciesTaskFertilizer speciesTask = form.getSpeciesTask();
        long speciesId = speciesTask.getSpeciesId();
        long taskId = speciesTask.getTaskId();
        List<SpeciesTaskFertilizer> existedST = speciesTaskFertilizerDAO.findBySpeciesTask(speciesId, taskId);
        if (existedST == null) {
            speciesTask.setTaskId(task.getId());
        } else {
//            todoexistedST.setDescription(speciesTask.getDescription());
//            speciesTask = existedST;
        }
        speciesTaskFertilizerDAO.saveOrUpdate(speciesTask);

        return speciesTask.getId();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SpeciesTaskFertilizer findOrCreate(SpeciesTaskFertilizer speciesTask) {
        List<SpeciesTaskFertilizer> existedSpeciesTask = speciesTaskFertilizerDAO.findBySpeciesTask(speciesTask.getSpeciesId(), speciesTask.getTaskId());
        if (existedSpeciesTask != null) {
            //todo return existedSpeciesTask;
        }

        speciesTaskFertilizerDAO.insert(speciesTask);
        return speciesTask;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SpeciesTaskFertilizer create(SpeciesTaskFertilizer speciesTask) {
        speciesTaskFertilizerDAO.insert(speciesTask);
        return speciesTask;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(long fertilizerId, long speciesTaskId) {
        speciesTaskFertilizerDAO.deleteByFertilizerSpecies(fertilizerId, speciesTaskId);
    }

}
