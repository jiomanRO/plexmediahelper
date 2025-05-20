package com.john.plexmediahelper.scheduling;

import com.john.plexmediahelper.scheduling.model.ScheduledExecutionRun;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ExecutionRunHistory {

    ArrayList<ScheduledExecutionRun> runs;

    public ExecutionRunHistory() {
        runs = new ArrayList<>();
    }

    public ArrayList<ScheduledExecutionRun> getRuns() {
        return runs;
    }

    public void setRuns(ArrayList<ScheduledExecutionRun> runs) {
        this.runs = runs;
    }
}
