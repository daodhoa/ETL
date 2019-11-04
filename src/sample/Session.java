package sample;

import com.dataflow.DataFlow;

public final class Session {
    private static DataFlow dataFlow;

    private Session() {}

    public static DataFlow getDataFlow() {
        if(dataFlow == null) {
            dataFlow = new DataFlow();
        }
        return dataFlow;
    }

}
