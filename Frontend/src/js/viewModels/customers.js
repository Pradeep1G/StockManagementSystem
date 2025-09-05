define(['../accUtils', 'knockout'],
  function (accUtils, ko) {
    function CustomerViewModel() {

      this.customers = ko.observableArray([])

      this.firstName = ko.observable('');
      this.lastName = ko.observable('');
      this.phoneNumber = ko.observable('');
      this.emailId = ko.observable('');
      this.password = ko.observable('');

          if(localStorage.getItem("userType")==null){self.router.go({path: 'inventory'})}


      this.addCustomer = function () {
        const customer = {
          firstName: this.firstName(),
          lastName: this.lastName(),
          phoneNumber: this.phoneNumber(),
          emailId: this.emailId(),
          password: this.password()
        };
        fetch('http://gvpradee-fv9rjb4:8085/customer', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(customer)
        })
          .then(response => response.json())
          .then(data => {
            alert('Response : ' + JSON.stringify(data));
          })
          .catch(error => {
            alert('Failed to add customers: ' + error);
          });

      }

      this.deleteCustomer = function () {
        alert("It's a joke!")
      }

      this.listCustomers = function () {
        fetch('http://gvpradee-fv9rjb4:8085/customer/list', {
          method: 'GET'
        })
          .then(response => response.json())
          .then(data => {
            this.customers(data.data);
          })
          .catch(error => {
            alert('Failed to list customers: ' + error);
          });
      }
      this.listCustomers();

      this.updateCustomer = function () {
        alert("It's a joke!")
      }

      this.connected = () => {
        accUtils.announce('Customers page loaded.', 'assertive');
        document.title = "Customers";
      };

      this.disconnected = () => {
      };

      this.transitionCompleted = () => {
      };
    }

    return CustomerViewModel;
  }
);
