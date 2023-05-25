console.log("Script loaded!");

// Initial load of the movies - discover
const urlGetType = "https://movies-gateway-hoth42g.ew.gateway.dev/getType";
fetch(urlGetType)
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Failed to retrieve movie data');
        }
    })
    .then(movieData => {
        const filteredMovies = filterMovieProperties(movieData);
        updateMovieDetails(filteredMovies);
        console.log(`Discover Movies:`, movieData);
    })
    .catch(error => {
        console.error(error);
    });

// Loading movie data based on chosen category from navbar
const popularMovies = document.getElementById('popular');
const topRatedMovies = document.getElementById('top_rated');
const upcomingMovies = document.getElementById('upcoming');
popularMovies.addEventListener('click', () => fetchMovies('popular'));
topRatedMovies.addEventListener('click', () => fetchMovies('top_rated'));
upcomingMovies.addEventListener('click', () => fetchMovies('upcoming'));

function fetchMovies(category) {
    const endpoint = urlGetType + '?endpoint=' + category;
    fetch(endpoint)
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
            console.log(`${category} Movies:`, data);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}
function filterMovieProperties(movieData) {
    const filteredMovies = [];
    for (let i = 0; i < movieData.results.length; i++) {
        const movie = movieData.results[i];
        const filteredMovie = {
            id: movie.id,
            title: movie.title,
            poster_path: movie.poster_path,
            overview: movie.overview,
            release_date: movie.release_date,
            vote_count: movie.vote_count,
            vote_average: movie.vote_average
        };
        filteredMovies.push(filteredMovie);
    }
    return filteredMovies;
}

function updateMovieDetails(movieData) {
    // Get movie container element, clear it
    const movieContainer = document.getElementById('movieContainer');
    movieContainer.innerHTML = '';

    // Loop through the filtered movie data and create HTML elements
    for (let i = 0; i < movieData.length; i++) {
        const movie = movieData[i];

        // Create movie card element
        const movieCard = document.createElement('div');
        movieCard.classList.add('movieCard');

        // Create hidden movie id element
        const movieId = document.createElement('p');
        movieId.classList.add('movieId');
        movieId.textContent = movie.id;
        movieId.style.display = 'none';
        movieCard.appendChild(movieId);

        // Create movie title element
        const titleElement = document.createElement('h2');
        titleElement.classList.add('movieTitle');
        titleElement.textContent = movie.title;
        movieCard.appendChild(titleElement);

        // Create movie poster element
        const posterElement = document.createElement('img');
        posterElement.classList.add('moviePoster');

        if (movie.poster_path) {
            posterElement.src = 'https://image.tmdb.org/t/p/w500' + movie.poster_path;
            posterElement.alt = movie.title + ' Poster';
        } else {
            posterElement.src = 'no-poster.png';
            posterElement.alt = 'No poster available';
        }
        movieCard.appendChild(posterElement);

        // Create movie overview element
        const overviewElement = document.createElement('p');
        overviewElement.classList.add('movieOverview');
        overviewElement.textContent = movie.overview;
        movieCard.appendChild(overviewElement);

        // Create movie information elements
        const infoElement = document.createElement('div');
        infoElement.classList.add('movieInfo');

        // Create release date element
        const releaseDateElement = document.createElement('p');
        releaseDateElement.textContent = 'Release Date: ' + movie.release_date;
        infoElement.appendChild(releaseDateElement);

        // Create vote count element
        const voteCountElement = document.createElement('p');
        voteCountElement.textContent = 'Vote Count: ' + movie.vote_count;
        infoElement.appendChild(voteCountElement);

        // Create vote average element
        const voteAverageElement = document.createElement('p');
        voteAverageElement.textContent = 'Vote Average: ' + movie.vote_average;
        infoElement.appendChild(voteAverageElement);

        movieCard.appendChild(infoElement);

        // Append movie card to the container
        movieContainer.appendChild(movieCard);
    }
}