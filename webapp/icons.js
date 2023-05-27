// =======================  ICON CLICK LISTENER  ============================= //

const movieListsUrl = "URL_MOVIE_LISTS_PLACEHOLDER";

function handleIconClick(iconElement, iconType, uid, movieId) {
    const isIconChanged = iconElement.dataset.iconChanged === 'true';
    const imageSuffix = isIconChanged ? '' : '-full';

    iconElement.src = `${iconType}${imageSuffix}.png`;
    iconElement.dataset.iconChanged = isIconChanged ? 'false' : 'true';

    const icon = document.querySelector(`[data-icon-target="${iconElement.dataset.iconTarget}"]`);
    if (icon) {
        icon.src = `${iconType}${imageSuffix}.png`;
        icon.dataset.iconChanged = isIconChanged ? 'false' : 'true';
    }

    if (iconType === 'add') {
        if (isIconChanged) {
            fetch(movieListsUrl +'?uid=' + uid + '&movieId=' + movieId + '&list=watchlist&action=remove', {
                method: 'POST'
            })
                .then(response => {
                    if (response.ok) {
                        console.log("Movie has been removed from watchlist!")
                        return response.text();
                    } else {
                        console.log(response)
                        throw new Error(`Error: ${response.status}`);
                    }
                })
                .catch(error => {
                    // Handle network or other errors
                    console.error('Error:', error.message);
                });
        } else {
            fetch(movieListsUrl +'?uid=' + uid + '&movieId=' + movieId + '&list=watchlist&action=store', {
                method: 'POST'
            })
                .then(response => {
                    if (response.ok) {
                        console.log("Movie has been added to watchlist!")
                        return response.text();
                    } else {
                        console.log(response)
                        throw new Error(`Error: ${response.status}`);
                    }
                })
                .catch(error => {
                    // Handle network or other errors
                    console.error('Error:', error.message);
                });
        }
    } else if (iconType === 'heart') {
        if (isIconChanged) {
            fetch(movieListsUrl +'?uid=' + uid + '&movieId=' + movieId + '&list=favorites&action=remove', {
                method: 'POST'
            })
                .then(response => {
                    if (response.ok) {
                        console.log("Movie has been removed from favorites!")
                        return response.text();
                    } else {
                        console.log(response)
                        throw new Error(`Error: ${response.status}`);
                    }
                })
                .catch(error => {
                    // Handle network or other errors
                    console.error('Error:', error.message);
                });
        } else {
            fetch(movieListsUrl +'?uid=' + uid + '&movieId=' + movieId + '&list=favorites&action=store', {
                method: 'POST'
            })
                .then(response => {
                    if (response.ok) {
                        console.log("Movie has been added to favorites!")
                        return response.text();
                    } else {
                        console.log(response)
                        throw new Error(`Error: ${response.status}`);
                    }
                })
                .catch(error => {
                    console.error('Error:', error.message);
                });
        }
    }
}

// generate unique ID for icons
let id = 0;

function generateUniqueId() {
    return id++;
}