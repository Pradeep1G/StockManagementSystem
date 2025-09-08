define(["../accUtils", "knockout", "ojs/ojarraydataprovider"], function (
  accUtils,
  ko,
  ArrayDataProvider
) {
  function CustomerViewModel() {
    this.customers = ko.observable();

    this.firstName = ko.observable("");
    this.lastName = ko.observable("");
    this.phoneNumber = ko.observable("");
    this.emailId = ko.observable("");
    this.password = ko.observable("");

    var self = this;
    self.mostTransactedStocks = ko.observableArray([]);
    self.leastTransactedStocks = ko.observableArray([]);


    if (localStorage.getItem("userType") == null) {
      self.router.go({ path: "inventory" });
    }

    this.addCustomer = function () {
      const customer = {
        firstName: this.firstName(),
        lastName: this.lastName(),
        phoneNumber: this.phoneNumber(),
        emailId: this.emailId(),
        password: this.password(),
      };
      fetch("http://gvpradee-fv9rjb4:8085/customer", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(customer),
      })
        .then((response) => response.json())
        .then((data) => {
          alert("Response : " + JSON.stringify(data));
        })
        .catch((error) => {
          alert("Failed to add customers: " + error);
        });
    };

    this.deleteCustomer = function () {
      alert("It's a joke!");
    };

    this.listCustomers = function () {
      fetch("http://gvpradee-fv9rjb4:8085/customer/listWithAssets", {
        method: "GET",
      })
        .then((response) => response.json())
        .then((data) => {
          this.customers(new ArrayDataProvider(data.data, { keyAttributes: 'customerId' }));
        })
        .catch((error) => {
          alert("Failed to list customers: " + error);
        });
    };
    this.listCustomers();

    this.updateCustomer = function () {
      alert("It's a joke!");
    };

    self.fetchMostTransacted = function() {
      fetch('http://gvpradee-fv9rjb4:8087/stock/mostTransacted')
        .then(res => res.json())
        .then(data => {
          if (data.status === 'success') {
            self.mostTransactedStocks(data.data || []);
          } else {
            self.mostTransactedStocks([]);
          }
        });
    };
    self.fetchLeastTransacted = function() {
      fetch('http://gvpradee-fv9rjb4:8087/stock/leastTransacted')
        .then(res => res.json())
        .then(data => {
          if (data.status === 'success') {
            self.leastTransactedStocks(data.data || []);
          } else {
            self.leastTransactedStocks([]);
          }
        });
    };

    this.connected = () => {
      accUtils.announce("Customers page loaded.", "assertive");
      document.title = "Customers";
      self.fetchMostTransacted();
      self.fetchLeastTransacted();
    };

    this.disconnected = () => {};

    this.transitionCompleted = () => {};
  }

  return CustomerViewModel;
});
