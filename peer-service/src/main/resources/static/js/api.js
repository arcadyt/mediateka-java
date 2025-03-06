// API client for interacting with the backend
const API = (function() {
    const BASE_URL = '/api';

    return {
        // Get a list of entities of the specified type
        getEntities: function(entityType) {
            const pathMap = {
                'directory': 'directories',
                'media': 'media'
            };

            const path = pathMap[entityType] || entityType;
            return $.ajax({
                url: `${BASE_URL}/${path}`,
                method: 'GET'
            });
        },

        // Get available category types
        getCategoryTypes: function() {
            return $.ajax({
                url: `${BASE_URL}/category-types`,
                method: 'GET'
            });
        },

        // Create a new directory with direct category type
        createDirectory: function(directoryData) {
            return $.ajax({
                url: `${BASE_URL}/directories`,
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(directoryData)
            });
        },

        // Get related entities
        getRelatedEntities: function(url) {
            return $.ajax({
                url: url,
                method: 'GET'
            });
        }
    };
})();