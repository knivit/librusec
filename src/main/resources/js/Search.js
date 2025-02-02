"use strict";

var search = {
    root: {},

    init: function(root) {
        this.root = document.getElementById(root);
    },

    show: function(response) {
        search.root.innerHTML = response;
    }
};
