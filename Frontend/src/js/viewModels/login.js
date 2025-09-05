define([
  "knockout",
  "../accUtils",
  "../appController",
  "ojs/ojknockout",
  "ojs/ojbutton",
  "ojs/ojformlayout",
  "ojs/ojinputtext",
  "ojs/ojselectsingle",
  "ojs/ojoption",
  "ojs/ojlabel",
  "ojs/ojmessages",
], function (ko, accUtils, appController) {
  function LoginViewModel(params) {
    const self = this;
    self.router = params && params.router;
    if (localStorage.getItem("user") != null) {
      self.router.go({ path: "dashboard" });
    }
    self.email = ko.observable("");
    self.password = ko.observable("");
    self.firstName = ko.observable("");
    self.lastName = ko.observable("");
    self.phoneNumber = ko.observable("");
    self.emailId = ko.observable("");
    self.passwords = ko.observable("");
    self.userType = ko.observable("customer");
    self.submitting = ko.observable(false);
    self.messages = ko.observableArray([]);

    self.userTypes = [
      { value: "customer", label: "Customer" },
      { value: "admin", label: "Admin" },
    ];

    self.isLogin = ko.observable(true);
    self.toggleLogin = function () {
      self.isLogin(!self.isLogin());
    };

    self.connected = () => {
      console.log("params in login ViewModel:", params);

      accUtils.announce("Login page loaded.", "assertive");
      document.title = "Login";
      self.messages([]);
    };

    function addErrorMessage(summary, detail) {
      self.messages([
        {
          severity: "error",
          summary,
          detail,
          autoTimeout: 0,
        },
      ]);
    }

    async function loginCustomer() {
      const resp = await fetch("http://gvpradee-fv9rjb4:8085/customer/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          emailId: self.email(),
          password: self.password(),
        }),
      });
      const data = await resp.json().catch(() => ({}));
      console.log(data);
      if (!resp.ok || data.status !== "success") {
        throw new Error((data && data.message) || "Login failed.");
      }
      localStorage.setItem("user", self.email());
      appController.setUserLogin(self.email());
      appController.setNavData("customer");
      self.router.go({ path: "dashboard" });
    }

    async function signUpCustomer() {
      const customer = {
        firstName: self.firstName(),
        lastName: self.lastName(),
        phoneNumber: self.phoneNumber(),
        emailId: self.emailId(),
        password: self.passwords(),
      };
      console.log(customer);
      const resp = await fetch("http://gvpradee-fv9rjb4:8085/customer", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(customer),
      });
      const data = await resp.json().catch(() => ({}));
      console.log(data);
      if (!resp.ok || data.status !== "success") {
        throw new Error((data && data.message) || "Sign In failed.");
      }
      localStorage.setItem("user", self.emailId());
      appController.setUserLogin(self.emailId());
      appController.setNavData("customer");
      self.router.go({ path: "dashboard" });
    }

    async function adminLogin() {
      const admin = {
        emailId: self.email(),
        password: self.password(),
      };
      console.log(admin);
      const resp = await fetch("http://gvpradee-fv9rjb4:8085/customer/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(admin),
      });
      const data = await resp.json().catch(() => ({}));
      console.log(data);
      if (!resp.ok || data.status !== "success") {
        throw new Error((data && data.message) || "Log In failed.");
      }
      localStorage.setItem("user", self.email());
      localStorage.setItem("userType", "Admin");
      appController.setUserLogin(self.email());
      appController.setNavData("Admin");

      // alert("Sign Up Success");
      self.router.go({ path: "inventory" });
    }

    self.submit = async () => {
      self.messages([]);
      if (!self.email() && !self.emailId()) {
        addErrorMessage(
          "Missing information",
          "Please enter your email and password."
        );
        return;
      }
      self.submitting(true);
      try {
        if (self.userType() === "admin") {
          alert("admin loged in");
          await adminLogin();
        } else if (self.firstName() && self.lastName()) {
          alert("user signed in");
          await signUpCustomer();
        } else {
          alert("user logged in");
          await loginCustomer();
        }
      } catch (e) {
        addErrorMessage(
          "Login error",
          e.message || "Invalid email or password."
        );
      } finally {
        self.submitting(false);
      }
    };

    self.disconnected = () => {};
    self.transitionCompleted = () => {};
  }
  return LoginViewModel;
});
