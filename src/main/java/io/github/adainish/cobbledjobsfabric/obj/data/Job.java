package io.github.adainish.cobbledjobsfabric.obj.data;

import io.github.adainish.cobbledjobsfabric.CobbledJobsFabric;
import io.github.adainish.cobbledjobsfabric.obj.configurabledata.ConfigurableJob;




public class Job
{
    public String jobName = "UNDEFINED";
    public int level = 1;
    public double experience = 0;

    public boolean hasJob = false;

    public Job()
    {

    }

    public void increaseLevel()
    {
        if (canLevelUp())
            this.level ++;
    }

    public void resetLevel()
    {
        this.level = 1;
    }

    public void resetExperience()
    {
        this.experience = 0;
    }

    public void increaseExperience(double amount)
    {
        this.experience += amount;
    }

    public int nextLevel()
    {
        return level + 1;
    }

    public boolean maxLevel() {
        boolean max = true;
        ConfigurableJob configurableJob = getConfigurableJob();
        if (configurableJob != null) {
            return configurableJob.levels.containsKey(nextLevel());
        }

        return max;
    }
    public boolean canLevelUp()
    {
        if (maxLevel())
            return false;
        ConfigurableJob configurableJob = getConfigurableJob();
        if (configurableJob != null) {
            return getConfigurableJob().levels.get(nextLevel()).requiredExperience <= this.experience;
        }
        return false;
    }

    public ConfigurableJob getConfigurableJob()
    {
        if (CobbledJobsFabric.jobsConfig.jobManager.configurableJobs.containsKey(jobName)) {
            return CobbledJobsFabric.jobsConfig.jobManager.configurableJobs.get(jobName);
        }
        return null;
    }
}
