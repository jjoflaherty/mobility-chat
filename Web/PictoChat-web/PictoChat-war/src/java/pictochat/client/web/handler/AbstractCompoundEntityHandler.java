package pictochat.client.web.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kpoint.javaee.web.handler.forms.IndexedFormDataObject;

/**
 *
 * @author Steven
 */
public abstract class AbstractCompoundEntityHandler<T, U extends IndexedFormDataObject<V>, V extends Number>
{
    private Map<V, T> dataModelMap = new HashMap<V, T>();
    private List<U> formModels = new ArrayList<U>();
    private boolean init = false;

    public abstract U convertToFormModel(T model);
    public abstract V convertDataModelToKey(T model);

    public abstract List<U> getData();
    public abstract void setData(List<U> data);

    public void addModels(List<T> models) {
        for (T model : models)
            this.addModel(model);

        this.finish();
    }
    public void addModel(T model) {
        this.dataModelMap.put(this.convertDataModelToKey(model), model);
        this.formModels.add(this.convertToFormModel(model));
    }
    public T getDataModelFromFormModel(U model) {
        return this.getDataModelFromKey(model.getKeyId());
    }
    public T getDataModelFromKey(V key) {
        return this.dataModelMap.get(key);
    }

    public boolean exists(T model) {
        return this.keyExists(this.convertDataModelToKey(model));
    }
    public boolean keyExists(V key) {
        return this.dataModelMap.containsKey(key);
    }


    public void clearModels() {
        this.dataModelMap.clear();
        this.formModels.clear();

        this.init = false;
    }

    protected List<U> getFormModels() {
        return Collections.unmodifiableList(this.formModels);
    }
    public int getModelCount() {
        return this.dataModelMap.size();
    }

    public void finish() {
        this.init = true;
    }

    public boolean isInit() {
        return this.init;
    }

    public boolean isEmpty() {
        this.getData();
        return (this.dataModelMap == null || this.dataModelMap.isEmpty());
    }
    public boolean getIsEmpty() {
        return this.isEmpty();
    }
}
