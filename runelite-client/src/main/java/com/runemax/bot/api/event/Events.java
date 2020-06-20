package com.runemax.bot.api.event;

//@Slf4j
//public class Events {
//    @Inject
//    private static EventBus eventBus;
//
//    private static final Set<Object> listeners = new HashSet<>();
//
//    public static void register(Object listener) {
////        eventBus.register(listener);
////        eventBus.unregister();
//        listeners.add(listener);
//        log.info("event bus registered " + listener.getClass().getSimpleName());
//    }
//
//    public static void unregister(Object listener) {
//        try {
//            eventBus.unregister(listener);
//            log.info("unregistered " + listener.getClass().getSimpleName());
//        } catch (IllegalArgumentException e) {
//            log.warn("prob tried to unregister something thats not registered" + listener.getClass().getSimpleName(), e);
//        }
//
////        listeners.remove(listener);
//    }
//
//    public static void post(Object event) {
////        eventBus.post(event);
//    }
//
////    public static void clear() {
////        List<Object> copy = ImmutableList.copyOf(listeners);
////        for (Object listener : copy) {
////            try {
////                unregister(listener);
////            }catch (Exception e) {
////                log.warn("some exception when unregistering: " + e.getMessage() + " class:" + listener.getClass().getSimpleName());
////            }
////        }
////    }
//}
