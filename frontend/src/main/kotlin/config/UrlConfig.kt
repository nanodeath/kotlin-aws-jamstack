package config

object UrlConfig {
    // The backend stack emits (exports) the value to use here when deploying.
    // YOU MUST SET THIS FOR AJAX CALLS TO WORK.
    //
    // We probably could automatically retrieve this, but it would require performing the entire backend
    // build + deploy before the frontend build + deploy, and that would slow down the development process.
    const val apiRoot = "https://tk.execute-api.us-west-2.amazonaws.com/prod/"
}