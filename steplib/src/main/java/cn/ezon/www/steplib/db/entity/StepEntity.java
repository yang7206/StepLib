package cn.ezon.www.steplib.db.entity;


import cn.ezon.www.steplib.db.utils.DatabaseField;

public class StepEntity {
    @DatabaseField(id = true, canBeNull = false)
    private Integer id;
    @DatabaseField
    private Integer step;
    @DatabaseField
    private String day;
    @DatabaseField
    private Integer activeMin;
    @DatabaseField
    private String hourStep = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Integer getActiveMin() {
        return activeMin;
    }

    public void setActiveMin(Integer activeMin) {
        this.activeMin = activeMin;
    }

    public void stepIncrease() {
        if (step == null) {
            step = 0;
        }
        step++;
    }

    public void activeMinIncrease() {
        if (activeMin == null) {
            activeMin = 0;
        }
        activeMin++;
    }

    public String getHourStep() {
        return hourStep;
    }

    public void setHourStep(String hourStep) {
        this.hourStep = hourStep;
    }

    @Override
    public String toString() {
        return "StepEntity{" +
                "id=" + id +
                ", step=" + step +
                ", day='" + day + '\'' +
                ", activeMin=" + activeMin +
                '}';
    }
}
