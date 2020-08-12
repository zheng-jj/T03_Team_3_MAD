# T03_Team_3_MAD

Team members

Zheng Jiong Jie S10195565

Swah Jian Oon S10195746

Chua Qi Heng S10195642

Christopher Tey Weixian S10193226

# Description of app

This application is one which enables users to view various books and find new books to read. Users can also use this 
application as a platform to post reviews on the books they have read, or save the books they have interest in in a list to refer to later on. 
This application also allows users to manage the books they are interested in, or view the information on other book titles.

# Contributions

# Zheng Jiong Jie

Basic Features (Completed in Assignment 1):
---------------------

Set up how fragments will be displayed

Created User page

Created Logged in user page

Created View all user's favourite books page

Created Home page

Allow user to view own data or other user's data(load data from database)

Update user data to database(update content in database)

Allow users to make changes to user's profile(image/name/description)

Allow users to upload new profile picture from gallery and add to internal storage

Allow users to follow other users(and store in database)

Allow users to add books to their favourite books list(and store in database)

Recyclerview adapters for "AdapterBookMain", "AdapterFavBooksList", "AdapterReviewForUSer", "AdapterUserMain"

Implemented on-click listeners which brings users to where they clicked for the various adapters

Allow users to manage their current list of books(remove/add)

Created Bottom navigation bar

LogOut

Custom Backstack

Navigation

Added upvoting of reviews

Advanced Features (Completed in Assignment 2):
---------------------

Moved all user account data to firestore

Moved user profile image to firebase storage

Moved user favourite books and follow users to firestore

Moved reviews to firestore

Enable notifications based on user actions (when users you follow do something, notifications are sent)

Settings page to edit notifications to be recieved

Async method to retrieve books from Google Books API

Async method to load book covers from respective URL links

Created page to get details on where books can be obtained

Async methods to get book prices from different vendors (from real online offers) using DirectTextBook ApI and load data in a recycler view

Async methods to get eBook availability from Google Books API

Get book availability from National Library Board (using NLB API) for each copy of the book available and load data in a recycler view

Added Feed page to know get more information on people you are following

Feed page and review page uses firestore recycler adapter which has updates the recyclerview real-time

Added email to user regarding admin actions from admin account using javamail

Report form to report user abuse which can be banned by admin


# Swah Jian Oon
# Things Implemented:
---------------------
Imported precreated database into android studio

Created local database - DatabaseAccess & DatabaseOpenHelper

Created methods to extract data from database

Created methods to store data into database

Created adapters for AdapterReview

Created review page

Created add review page 

Linked book information fragment to its review page 

Linked clicking on name of reviewer on review page to the reviewer's profile

Added Feed page for following people you know

Changed from normal recyclerview to firestore recyclerview for real-time updates for feed and reviews

Added upvoting of reviews

Implemented emailing methods

Created popular books recyclerview for homepage

Report users page

# Chua Qi Heng
Basic Features (Completed in Assignment 1):
---------------------
Added parcelable to Book and User classes so people can bring objects to another fragment

Added clicking functionality to adapters

Added search functionality

Added method to save images to internal storage

Added method to set image with String.

Passed object information to Book and User Info so that other people can use the info

Created methods to search database for the search functionality

Created User, Book and Searchclass models

Advanced Features (Completed in Assignment 2):
--------
Added Upload Books feature

Added Verify Books feature

Added Ban Users Feature

Added Unban Users Feature

Added View Reports Feature

Brought Search over to API and Firestore

Modified code so that API books searched can get info and pass to bookinfo fragment

Allowed Uploaded books to interact properly with actions

ASync method to retrieve specific book from google books api

Implemented Admin and User roles to user




# Christopher Tey Weixian
# Things Implemented:
---------------------
Created Shared Preferences to check if user is logged in or not

Register Page

Created Firebase database for creation of new users

Login Page

Created method to insert user's data from firebase database into local database

Created method to check if user's data existed in the local database or not

Linked Login page to register page and home page 

Created Genre recycler view in homepage.

Created a recycler view for books based on the genre that they clicked on the previous page

Linked the 2 recycler view fragments to each other and linked the bookbygenre fragment to bookinfo fragment

Created the recycler view for genre in the bookinfo fragment

Created the adaptors of "AdaptorGenreInHomeFragment" ,"AdaptorGenre" and "AdaptorToViewBookBasedOnGenre".

Advanced Features (Completed in Assignment 2):
--------
Login otp page

Reset password page

Check deviceid when auto logging in

Retrieve all the books by the genre pressed by the user from the API

Mail api file

Added the need to enter an unique key in account registering page

Account created will be given user role and email will be send with account key to the user after registering.

Change the retrival of the recommedated books displayed from local database to firestore
