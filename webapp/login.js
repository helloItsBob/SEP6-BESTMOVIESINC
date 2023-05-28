import {
    initializeApp
} from "https://www.gstatic.com/firebasejs/9.22.0/firebase-app.js";
import {
    createUserWithEmailAndPassword,
    getAuth,
    onAuthStateChanged,
    signInWithEmailAndPassword
} from "https://www.gstatic.com/firebasejs/9.22.0/firebase-auth.js";

export let uid;

import { fetchFavoriteList, fetchWatchList } from './retrieveLists.js';
// web app's Firebase configuration
const firebaseConfig = {
    apiKey: "FIREBASE_API_KEY",
    authDomain: "FIREBASE_AUTH_DOMAIN",
    projectId: "FIREBASE_PROJECT_ID",
    storageBucket: "FIREBASE_STORAGE_BUCKET",
    messagingSenderId: "FIREBASE_MESSAGING_SEND_ID",
    appId: "FIREBASE_APP_ID",
    measurementId: "FIREBASE_MEASURE_ID"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = getAuth(app);


//**********************  SIGN IN AN EXISTING USER  **************************//

const loginForm = document.getElementById('loginForm');
loginForm.addEventListener('submit', function (event) {
    event.preventDefault(); // Prevent form submission

    const usernameInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const email = usernameInput.value;
    const password = passwordInput.value;

    // Call the Firebase Authentication method
    signInWithEmailAndPassword(auth, email, password)
        .then((userCredential) => {
            // User signed in successfully
            const user = userCredential.user;
            console.log("User signed in:", user.uid);
        })
        .catch((error) => {
            // Error occurred during sign in
            const errorCode = error.code;
            const errorMessage = error.message;
            console.error("Sign in error:", errorCode, errorMessage);
            const loginMessage = document.getElementById('loginMessage');
            loginMessage.textContent = 'Invalid username or password!';
            loginMessage.style.color = 'red';
            loginMessage.style.display = 'block';

            // Hide message after 3 seconds
            setTimeout(function () {
                loginMessage.style.display = 'none';
            }, 3000);
        });
    usernameInput.value = '';
    passwordInput.value = '';
});


//********************  HANDLE USER SIGN UP  ********************************//

const registrationForm = document.getElementById('registrationForm');
registrationForm.addEventListener('submit', function (event) {
    event.preventDefault();

    const usernameInput = document.getElementById('usernameReg');
    const emailInput = document.getElementById('emailReg');
    const passwordInput = document.getElementById('passwordReg');
    const username = usernameInput.value;
    const email = emailInput.value;
    const password = passwordInput.value;

    const createUserUrl = 'URL_CREATE_USER_PLACEHOLDER';

    fetch(createUserUrl + '?username=' + username)
        .then(response => {
            if (response.ok) {
                // Username is available, proceed with Firebase account creation
                createUserWithEmailAndPassword(auth, email, password)
                    .then((userCredential) => {
                        // User signed up successfully
                        const user = userCredential.user;
                        console.log("User signed up:", user.uid);

                        // save username and uid in neo4j
                        fetch(createUserUrl + '?uid=' + user.uid + '&username=' + username, {
                            method: 'POST'
                        })
                            .then(response => {
                                if (response.ok) {
                                    console.log(response)
                                    return response.text();
                                } else {
                                    console.log(response)
                                    throw new Error(`Error: ${response.status}`);
                                }
                            })
                            .then(data => {
                                console.log('User has been stored successfully:', data);
                            })
                            .catch(error => {
                                console.error('Error:', error.message);
                            });

                        const registrationMessage = document.getElementById('registrationMessage');
                        registrationMessage.textContent = 'Success!'
                        registrationMessage.style.color = 'green';
                        registrationMessage.style.display = 'block';

                        // Hide message after one second
                        setTimeout(function () {
                            registrationModal.style.display = 'none';
                            registrationMessage.style.display = 'none';
                        }, 1000);
                    })
                    .catch((error) => {
                        // Error occurred during sign up
                        const errorCode = error.code;
                        const errorMessage = error.message;
                        console.error("Sign up error:", errorCode, errorMessage);
                        // Display an error message or handle the error
                        const registrationMessage = document.getElementById('registrationMessage');
                        registrationMessage.textContent = 'Account with this email already exists!'
                        registrationMessage.style.color = 'red';
                        registrationMessage.style.display = 'block';

                        // Hide message after 3 seconds
                        setTimeout(function () {
                            registrationMessage.style.display = 'none';
                        }, 3000);
                    });
            } else {
                console.log("Error:", response.status);
                // Username already exists in Neo4j, display an error message
                const registrationMessage = document.getElementById('registrationMessage');
                registrationMessage.textContent = 'Account with this username already exists!';
                registrationMessage.style.color = 'red';
                registrationMessage.style.display = 'block';

                // Hide message after 3 seconds
                setTimeout(function () {
                    registrationMessage.style.display = 'none';
                }, 3000);
            }
        })
        .catch(error => {
            // Handle network or other errors
            console.error("Error:", error);
        });
    usernameInput.value = '';
    emailInput.value = '';
    passwordInput.value = '';
});


//*********************  LOG OUT THE USER  **********************************//

const logoutButton = document.getElementById('logoutButton');
logoutButton.addEventListener('click', function () {
    auth.signOut()
        .then(function () {
            console.log('Logout successful');
        })
        .catch(function (error) {
            console.log('Logout error', error);
        });
});


//****************  PERSONALIZED CONTENT BASED ON AUH STATE  *****************//

const welcome = document.getElementById('welcomeMessage');
const previousDisplayState = welcome.style.display;

export function checkIfUserSignedIn() {
    onAuthStateChanged(auth, async (user) => {
        const navbarLinks = document.getElementById('auth-navbarLinks');
        const loginDiv = document.getElementById('loginDiv');
        const watchlistIcon = document.getElementsByClassName('watchlistIcon');
        const favoritesIcon = document.getElementsByClassName('heartIcon');
        const movieCards = document.querySelectorAll('.movieCard');
        const loadingOverlay = document.getElementById("loading-overlay");


        if (user) {
            // user signed in
            uid = user.uid;
            console.log("User is signed in:", uid);
            // Perform actions for signed-in user
            navbarLinks.classList.remove('hidden');
            loginDiv.classList.add('hidden');

            // Start loading animation
            loadingOverlay.style.display = "flex";

            // Fetch user lists to change icon states further
            const favList = await fetchFavoriteList(uid);
            const watchList = await fetchWatchList(uid);
            // Loop through the watchlist icons and remove the 'hidden' class
            for (let i = 0; i < watchlistIcon.length; i++) {
                watchlistIcon[i].classList.remove('hidden');
            }

            // Loop through all cards and set watchlist icons to correct state according to the users' watchlist (true/false + pic)
            for (let i = 0; i < movieCards.length; i++) {
                if (watchList.includes(parseInt(movieCards[i].querySelector('.movieId').textContent))) {
                    movieCards[i].querySelector('.watchlistIcon').src = 'add-full.png';
                    movieCards[i].querySelector('.watchlistIcon').dataset.iconChanged = 'true';
                }
            }

            // Loop through the favorites icons and remove the 'hidden' class
            for (let i = 0; i < favoritesIcon.length; i++) {
                favoritesIcon[i].classList.remove('hidden');
            }

            // Loop through all cards and set favourite icons to correct state according to the users' favourite list (true/false + pic)
            for (let i = 0; i < movieCards.length; i++) {
                if (favList.includes(parseInt(movieCards[i].querySelector('.movieId').textContent))) {
                    movieCards[i].querySelector('.heartIcon').src = 'heart-full.png';
                    movieCards[i].querySelector('.heartIcon').dataset.iconChanged = 'true';
                }
            }

            // TODO: modify welcome message to a specific user
            // Hide message after 2 seconds
            setTimeout(function () {
                welcome.style.display = 'none';
            }, 2000);

            // Stop loading animation
            loadingOverlay.style.display = "none";

        } else {
            // User is signed out
            console.log("User is signed out");
            navbarLinks.classList.add('hidden');
            loginDiv.classList.remove('hidden');

            // Loop through the watchlist icons and add the 'hidden' class
            for (let i = 0; i < watchlistIcon.length; i++) {
                watchlistIcon[i].classList.add('hidden');
            }

            // Loop through the favorites icons and add the 'hidden' class
            for (let i = 0; i < favoritesIcon.length; i++) {
                favoritesIcon[i].classList.add('hidden');
            }

            // Hide message after 2 seconds
            setTimeout(function () {
                welcome.style.display = previousDisplayState;
            }, 2000);
        }
    });
}

checkIfUserSignedIn();