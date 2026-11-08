package ruipokoim.neonclient.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class EventBus {
    private final Map<Class<?>, List<ListenerEntry>> listeners = new HashMap<>();
    record ListenerEntry(Object instance, Method method) {}
    public void subscribe(Object object){
        for(Method method : object.getClass().getDeclaredMethods()){
            if(method.isAnnotationPresent(EventHandler.class) && method.getParameterCount() == 1){
                Class<?> type = method.getParameterTypes()[0];
                listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(new ListenerEntry(object, method));
            }
        }
    }
    public void unsubscribe(Object object){
        listeners.values().forEach(list -> list.removeIf(entry -> entry.instance() == object));
    }
    public void post(Object event){
        List<ListenerEntry> entries = listeners.get(event.getClass());
        if (entries == null) return;
        for(ListenerEntry entry : entries){
            try{
                entry.method().setAccessible(true);
                entry.method().invoke(entry.instance(), event);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}