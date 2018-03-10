// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  firebase: {
    apiKey: "AIzaSyDkiyHbWoGJ0iS6Pe67E9DAqwG4v3v6Ybs",
    authDomain: "business-chat-app.firebaseapp.com",
    databaseURL: "https://business-chat-app.firebaseio.com",
    projectId: "business-chat-app",
    storageBucket: "business-chat-app.appspot.com",
    messagingSenderId: "578742288376"
  }
};
