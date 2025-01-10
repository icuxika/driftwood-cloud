const eventTypes = ["API:NOT_LOGGED_IN"] as const;
type EventTypes = (typeof eventTypes)[number];

class EventEmitter {
    private listeners: Record<EventTypes, Set<(...args: any[]) => void>> = {
        "API:NOT_LOGGED_IN": new Set(),
    };

    on(event: EventTypes, listener: (...args: any[]) => void) {
        this.listeners[event].add(listener);
    }

    emit(event: EventTypes, ...args: any[]) {
        this.listeners[event].forEach((listener) => listener(...args));
    }
}

export default new EventEmitter();
