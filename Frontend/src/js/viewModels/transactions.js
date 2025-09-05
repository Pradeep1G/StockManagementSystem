define([
  "../accUtils",
  "knockout",
  "ojs/ojarraydataprovider",
  "ojs/ojtable"
], function (accUtils, ko, ArrayDataProvider) {
  function TransactionsViewModel(params) {
    var self = this;

        self.router = params && params.router;
    if(localStorage.getItem("user")==null){self.router.go({path: 'login'})}


    self.statusMessage = ko.observable('Loading...');
    self.transactionDataSource = ko.observable();

    self.loadTransactionDetails = function() {
      let email = localStorage.getItem('user');
      if (!email) {
        self.statusMessage('No user email found in localStorage.');
        return;
      }
      let custApiUrl = `http://gvpradee-fv9rjb4:8085/customer/${encodeURIComponent(email)}`;

      fetch(custApiUrl)
        .then(resp => {
          if (!resp.ok) throw new Error('Failed to fetch customer');
          return resp.json();
        })
        .then(json => {
          if (json.status !== "success") throw new Error(json.message || "Failed to fetch customer");
          let customerId = json.data.customerId;
          let transactionsApiUrl = `http://gvpradee-fv9rjb4:8085/customer/transactions/${customerId}`;
          return fetch(transactionsApiUrl);
        })
        .then(resp => {
          if (!resp.ok) throw new Error('Failed to fetch transactions');
          return resp.json();
        })
        .then(json => {
          if (json.status !== "success") throw new Error(json.message || "Failed to fetch transactions");
          let transactionsArray = json.data || [];
          console.log(JSON.stringify(json.data));
          self.statusMessage(`${transactionsArray.length} transaction(s) found.`);
          self.transactionDataSource(new ArrayDataProvider(transactionsArray, { keyAttributes: 'transactionId' }));
        })
        .catch(err => {
          self.statusMessage("Error: " + err.message);
        });
    };

    this.connected = () => {
      accUtils.announce('Transactions page loaded.', 'assertive');
      document.title = "Transactions";
      self.loadTransactionDetails();
    };
    this.disconnected = () => {};
    this.transitionCompleted = () => {};
  }
    return TransactionsViewModel;
  }
);
