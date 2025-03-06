// Entity management
const EntityManager = (function() {
    // Current entity type and selected entity
    let currentEntityType = 'directory';
    let selectedEntityId = null;

    // Entity type to endpoint mapping
    const endpointMap = {
        'directory': 'directories',
        'media': 'media'
    };

    // Entity type to embedded resource mapping
    const embeddedResourceMap = {
        'directory': 'directoryEntities',
        'media': 'mediaEntities'
    };

    // Utility functions
    const getEntityId = entity =>
        _.get(entity, '_links.self.href', '').split('/').pop() || entity.id;

    const getEntityDisplayName = entity => {
        const typeMap = {
            'directory': 'path',
            'media': 'relativeFilePath'
        };

        const propertyName = typeMap[currentEntityType];
        return _.get(entity, propertyName, `Unnamed ${_.capitalize(currentEntityType)}`);
    };

    const getPluralizedType = type =>
        type === 'directory' ? 'directories' : `${type}s`;

    return {
        // Get current entity type
        getCurrentEntityType: function() {
            return currentEntityType;
        },

        // Switch entity type
        switchEntityType: function(entityType) {
            // Update entity type
            currentEntityType = entityType;

            // Update header
            $('#entity-type-header').text(_.capitalize(getPluralizedType(entityType)));

            // Update "Add" button visibility based on entity type
            $('#add-entity-btn').toggle(entityType === 'directory');

            // Clear selected entity and details
            selectedEntityId = null;
            $('#entity-details').html('<div class="text-center text-muted">Select an item to view details</div>');
            $('#related-entities').html('<div class="list-group-item text-center text-muted">Select an item to view related entities</div>');

            // Load entities of the selected type
            this.loadEntities(entityType);
        },

        // Load entities of the specified type
        loadEntities: function(entityType) {
            $('#entity-list').html('<div class="list-group-item text-center text-muted">Loading...</div>');

            API.getEntities(entityType)
                .done(data => {
                    // Use the correct embedded resource name
                    const resourceName = embeddedResourceMap[entityType];
                    const entities = _.get(data, `_embedded.${resourceName}`, []);
                    this.displayEntities(entities);
                })
                .fail(xhr => {
                    $('#entity-list').html(`
                        <div class="list-group-item text-center text-danger">
                            Error loading ${getPluralizedType(entityType)}: ${xhr.status} ${xhr.statusText}
                        </div>
                    `);
                });
        },

        // Display entities in the entity list
        displayEntities: function(entities) {
            console.log('Displaying entities:', entities); // Debug

            if (_.isEmpty(entities)) {
                $('#entity-list').html(`
                    <div class="list-group-item text-center text-muted">
                        No ${getPluralizedType(currentEntityType)} found
                    </div>
                `);
                return;
            }

            const htmlItems = _.map(entities, entity => {
                const displayName = getEntityDisplayName(entity);
                // For directories, also show the category type
                const categoryInfo = currentEntityType === 'directory' && entity.defaultCategory ?
                    ` <span class="badge bg-secondary">${entity.defaultCategory}</span>` : '';

                return `
                    <div class="list-group-item" data-id="${getEntityId(entity)}" data-entity='${_.escape(JSON.stringify(entity))}'>
                        ${displayName}${categoryInfo}
                    </div>
                `;
            });

            $('#entity-list').html(htmlItems.join(''));

            // Add click event to entity items
            $('.entity-list .list-group-item').click(function() {
                $('.entity-list .list-group-item').removeClass('active');
                $(this).addClass('active');

                selectedEntityId = $(this).data('id');
                const entity = $(this).data('entity');
                EntityManager.displayEntityDetails(entity);
                EntityManager.loadRelatedEntities(entity);
            });
        },

        // Display entity details
        displayEntityDetails: function(entity) {
            const html = '<pre class="mb-0">' + JSON.stringify(entity, null, 2) + '</pre>';
            $('#entity-details').html(html);
        },

        // Load related entities
        loadRelatedEntities: function(entity) {
            const relationConfig = {
                'directory': { type: 'Media Files', linkPath: '_links.mediaFiles.href' },
                'media': { type: 'Directory', linkPath: '_links.directory.href' }
            };

            const config = relationConfig[currentEntityType];
            const relatedType = config.type;
            const url = _.get(entity, config.linkPath);

            $('#related-entities-header').text(relatedType);

            if (!url) {
                $('#related-entities').html(`
                    <div class="list-group-item text-center text-muted">
                        No related ${relatedType.toLowerCase()} available
                    </div>
                `);
                return;
            }

            $('#related-entities').html(`
                <div class="list-group-item text-center text-muted">
                    Loading related ${relatedType.toLowerCase()}...
                </div>
            `);

            API.getRelatedEntities(url)
                .done(data => {
                    this.displayRelatedEntities(data, relatedType);
                })
                .fail(xhr => {
                    $('#related-entities').html(`
                        <div class="list-group-item text-center text-danger">
                            Error loading related ${relatedType.toLowerCase()}: ${xhr.status} ${xhr.statusText}
                        </div>
                    `);
                });
        },

        // Display related entities
        displayRelatedEntities: function(data, relatedType) {
            const singularType = _.endsWith(relatedType.toLowerCase(), 's')
                ? _.trimEnd(relatedType.toLowerCase(), 's')
                : relatedType.toLowerCase();

            const pluralType = _.endsWith(relatedType.toLowerCase(), 's')
                ? relatedType.toLowerCase()
                : relatedType.toLowerCase() + 's';

            const entities = _.get(data, ['_embedded', pluralType]) ||
                             _.get(data, ['_embedded', singularType]) ||
                             [data];

            if (_.isEmpty(entities)) {
                $('#related-entities').html(`
                    <div class="list-group-item text-center text-muted">
                        No related ${relatedType.toLowerCase()} found
                    </div>
                `);
                return;
            }

            const entitiesList = _.isArray(entities) ? entities : [entities];
            const html = _.map(entitiesList, entity => {
                const displayName = entity.relativeFilePath || entity.path || 'Unnamed';
                return `
                    <div class="list-group-item">
                        ${displayName}
                    </div>
                `;
            }).join('');

            $('#related-entities').html(html);
        },

        // Handle adding a new directory
        addDirectory: function() {
            API.getCategoryTypes()
                .done(response => {
                    const categoryTypes = response.categoryTypes || [];

                    if (categoryTypes.length === 0) {
                        alert('No category types available');
                        return;
                    }

                    // Populate dropdown options for category types
                    const options = _.map(categoryTypes, type =>
                        `<option value="${type}">${type}</option>`
                    ).join('');

                    $('#categorySelect').html(options);

                    const directoryModal = new bootstrap.Modal(document.getElementById('directoryModal'));
                    directoryModal.show();
                })
                .fail(xhr => {
                    alert(`Error loading category types: ${xhr.status} ${xhr.statusText}`);
                });
        },

        // Save a new directory
        saveDirectory: function() {
            const path = $('#directoryPath').val();
            const categoryType = $('#categorySelect').val();

            if (!path || !categoryType) {
                alert('Please fill in all required fields');
                return;
            }

            const directoryData = {
                path: path,
                defaultCategory: categoryType
            };

            API.createDirectory(directoryData)
                .done(() => {
                    const directoryModal = bootstrap.Modal.getInstance(document.getElementById('directoryModal'));
                    directoryModal.hide();
                    $('#directoryForm')[0].reset();
                    this.loadEntities('directory');

                    // Add a temporary success message to the top of the entity list
                    $('#entity-list').prepend(`
                      <div class="alert alert-success alert-dismissible fade show" id="success-message">
                        Directory added successfully!
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                      </div>
                    `);
                    setTimeout(() => $('#success-message').fadeOut('slow', function() { $(this).remove(); }), 3000);
                })
                .fail(xhr => {
                    alert(`Error adding directory: ${xhr.status} ${xhr.statusText}`);
                });
        }
    };
})();