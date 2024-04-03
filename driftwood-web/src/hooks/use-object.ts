export const useObject = () => {
    const getPropertyValue = <T, K extends keyof T>(obj: T, key: K): T[K] =>
        obj[key];

    return {
        getPropertyValue,
    };
};
