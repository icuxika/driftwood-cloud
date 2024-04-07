export const useIcon = () => {
    const loadIonicons5 = async () => {
        const icons = await import("@vicons/ionicons5");
        return Object.values(icons);
    };
    return {
        loadIonicons5,
    };
};
