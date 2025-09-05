/**
 * @license
 * Copyright (c) 2014, 2025, Oracle and/or its affiliates.
 * Licensed under The Universal Permissive License (UPL), Version 1.0
 * as shown at https://oss.oracle.com/licenses/upl/
 * @ignore
 */
/*
 * Your incidents ViewModel code goes here
 */
define(['../accUtils'],
 function(accUtils) {
    function AdminViewModel() {

      this.connected = () => {
        accUtils.announce('Incidents page loaded.', 'assertive');
        document.title = "Incidents";
      };

      this.disconnected = () => {
      };

      this.transitionCompleted = () => {
      };
    }

    return AdminViewModel;
  }
);
