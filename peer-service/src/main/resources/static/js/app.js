// Main application controller
$(document).ready(function() {
    // Set up navigation events using common handler
    const setupNav = (selector, entityType) => {
        $(selector).click(() => {
            $('.nav-link').removeClass('active');
            $(selector).addClass('active');
            EntityManager.switchEntityType(entityType);
        });
    };

    setupNav('#nav-directories', 'directory');
    setupNav('#nav-categories', 'directory'); // Redirect to directories
    setupNav('#nav-media', 'media');

    // Set up add entity button
    $('#add-entity-btn').click(() => {
        const entityType = EntityManager.getCurrentEntityType();
        if (entityType === 'directory') {
            EntityManager.addDirectory();
        } else {
            alert(`Adding ${entityType}s is not supported in this interface.`);
        }
    });

    // Set up save button
    $('#saveDirectoryBtn').click(() => EntityManager.saveDirectory());

    // Load initial data - start with directories
    EntityManager.switchEntityType('directory');
});