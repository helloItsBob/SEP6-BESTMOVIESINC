// ======================================================================== //
// GET AUTH USER MOVIE LISTS

import {checkIfUserSignedIn, uid} from './login.js';
import {filterMovieProperties, updateMovieDetails} from './script.js';

const favoritesList = document.getElementById('favorites');
const watchlistList = document.getElementById('watchlist');
favoritesList.addEventListener('click', () => fetchList('favorites', uid));
watchlistList.addEventListener('click', () => fetchList('watchlist', uid));

const retrieveListsUrl = "URL_RETRIEVE_LISTS_PLACEHOLDER";

function fetchList(list, uid) {
    const url = retrieveListsUrl + '?list='
        + list + '&uid=' + uid;

    // show loading animation
    const loadingOverlay = document.getElementById("loading-overlay");
    loadingOverlay.style.display = "flex";

    fetch(url)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Failed to retrieve movie data');
            }
        })
        .then(data => {
            const filteredMovies = filterMovieProperties(data);
            updateMovieDetails(filteredMovies);
            console.log(`${list} Movies:`, data);

            // hide loading animation
            const loadingOverlay = document.getElementById("loading-overlay");
            loadingOverlay.style.display = "none";

            checkIfUserSignedIn();
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

export async function fetchFavoriteList(uid) {
    const url = retrieveListsUrl + '?list='
        + "favorites" + '&uid=' + uid;

    return fetch(url)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Failed to retrieve movie data');
            }
        })
        .then(data => {
            const filteredMovies = filterMovieProperties(data);
            return filteredMovies.map(object => object.id);
        });
}


export async function fetchWatchList(uid) {
    const url = retrieveListsUrl + '?list='
        + "watchlist" + '&uid=' + uid;

    return fetch(url)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Failed to retrieve movie data');
            }
        })
        .then(data => {
            const filteredMovies = filterMovieProperties(data);
            return filteredMovies.map(object => object.id);
        });
}