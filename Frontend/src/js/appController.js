define([
  "knockout",
  "ojs/ojcontext",
  "ojs/ojmodule-element-utils",
  "ojs/ojknockouttemplateutils",
  "ojs/ojcorerouter",
  "ojs/ojmodulerouter-adapter",
  "ojs/ojknockoutrouteradapter",
  "ojs/ojurlpathadapter",
  "ojs/ojresponsiveutils",
  "ojs/ojresponsiveknockoututils",
  "ojs/ojarraydataprovider",
  "ojs/ojdrawerpopup",
  "ojs/ojmodule-element",
  "ojs/ojknockout",
], function (
  ko,
  Context,
  moduleUtils,
  KnockoutTemplateUtils,
  CoreRouter,
  ModuleRouterAdapter,
  KnockoutRouterAdapter,
  UrlPathAdapter,
  ResponsiveUtils,
  ResponsiveKnockoutUtils,
  ArrayDataProvider
) {
  function ControllerViewModel() {
    this.KnockoutTemplateUtils = KnockoutTemplateUtils;

    // Handle announcements sent when pages change, for Accessibility.
    this.manner = ko.observable("polite");
    this.message = ko.observable();
    announcementHandler = (event) => {
      this.message(event.detail.message);
      this.manner(event.detail.manner);
    };
    localStorage.clear();
    document
      .getElementById("globalBody")
      .addEventListener("announce", announcementHandler, false);

    // Media queries for responsive layouts
    const smQuery = ResponsiveUtils.getFrameworkQuery(
      ResponsiveUtils.FRAMEWORK_QUERY_KEY.SM_ONLY
    );
    this.smScreen = ResponsiveKnockoutUtils.createMediaQueryObservable(smQuery);
    const mdQuery = ResponsiveUtils.getFrameworkQuery(
      ResponsiveUtils.FRAMEWORK_QUERY_KEY.MD_UP
    );
    this.mdScreen = ResponsiveKnockoutUtils.createMediaQueryObservable(mdQuery);

    this.navData = ko.observableArray([
      { path: "", redirect: "login" },
      {
        path: "login",
        detail: { label: "Login", iconClass: "oj-ux-ico-enter" },
      },
      {
        path: "about",
        detail: { label: "About", iconClass: "oj-ux-ico-information-s" },
      },
    ]);

    this.setNavData = function (userType) {
      if (userType === "Admin") {
        this.navData([
          { path: "", redirect: "inventory" },
          {
            path: "inventory",
            detail: { label: "Inventory", iconClass: "oj-ux-ico-box-grid" },
          },
          {
            path: "customers",
            detail: { label: "Report", iconClass: "oj-ux-ico-contact-group" },
          },
          {
            path: "about",
            detail: { label: "About", iconClass: "oj-ux-ico-information-s" },
          },
        ]);
      } else {
        this.navData([
          { path: "", redirect: "dashboard" },
          {
            path: "dashboard",
            detail: { label: "Dashboard", iconClass: "oj-ux-ico-box-grid" },
          },
          {
            path: "inventory",
            detail: { label: "Inventory", iconClass: "oj-ux-ico-store" },
          },
          {
            path: "transactions",
            detail: {
              label: "Transactions",
              iconClass: "oj-ux-ico-transfer-money",
            },
          },
          {
            path: "about",
            detail: { label: "About", iconClass: "oj-ux-ico-information-s" },
          },
        ]);
      }
    };

    const allRoutes = [
      { path: "", redirect: "login" },
      {
        path: "login",
        detail: { label: "Login", iconClass: "oj-ux-ico-enter" },
      },
      {
        path: "dashboard",
        detail: { label: "Dashboard", iconClass: "oj-ux-ico-box-grid" },
      },
      {
        path: "inventory",
        detail: { label: "Inventory", iconClass: "oj-ux-ico-store" },
      },
      {
        path: "transactions",
        detail: {
          label: "Transactions",
          iconClass: "oj-ux-ico-transfer-money",
        },
      },
      {
        path: "customers",
        detail: { label: "Report", iconClass: "oj-ux-ico-contact-group" },
      },
      {
        path: "reports",
        detail: { label: "Report", iconClass: "oj-ux-ico-information-s" },
      },
      {
        path: "about",
        detail: { label: "About", iconClass: "oj-ux-ico-information-s" },
      },
      // ...any other possible routes
    ];

    let router = new CoreRouter(allRoutes, {
      urlAdapter: new UrlPathAdapter(),
    });
    this.router = router;

    this.moduleAdapter = new ModuleRouterAdapter(router);

    this.selection = new KnockoutRouterAdapter(router);
    router.sync();

    // Setup the navDataProvider with the routes, excluding the first redirected
    // route.
    this.navDataProvider = ko.pureComputed(() => {
      return new ArrayDataProvider(this.navData().slice(1), {
        keyAttributes: "path",
      });
    });

    // Drawer
    self.sideDrawerOn = ko.observable(false);

    // Close drawer on medium and larger screens
    this.mdScreen.subscribe(() => {
      self.sideDrawerOn(false);
    });

    // Called by navigation drawer toggle button and after selection of nav drawer item
    this.toggleDrawer = () => {
      self.sideDrawerOn(!self.sideDrawerOn());
    };

    // Header
    // Application Name used in Branding Area
    this.appName = ko.observable("Stock Manager");
    // User Info used in Global Navigation area
    this.userLogin = ko.observable(localStorage.getItem("user") || "");

    // Add this function:
    this.setUserLogin = function (email) {
      this.userLogin(email);
    };

    this.handleSignOut = () => {
      localStorage.clear(); // or removeItem() for specific keys only
      this.navData([
        { path: "", redirect: "login" },
        {
          path: "login",
          detail: { label: "Login", iconClass: "oj-ux-ico-enter" },
        },
        {
          path: "about",
          detail: { label: "About", iconClass: "oj-ux-ico-information-s" },
        },
      ]);
      this.userLogin("");
      this.router.go({ path: "login" }); // clean SPA navigation
    };
    this.menuActionListener = (event) => {
      if (event && event.detail && event.detail.value === "out") {
        this.handleSignOut();
      }
    };

    // Optional: In ViewModel, register a flag so we only attach once
    this._menuListenerAttached = false;
    this.connected = () => {
      var menuElem = document.getElementById("menu1");
      if (menuElem && !this._menuListenerAttached) {
        menuElem.addEventListener("ojMenuAction", (event) => {
          if (event.detail && event.detail.value === "out") {
            this.handleSignOut();
          }
        });
        this._menuListenerAttached = true;
      }
    };

    // Footer
    this.footerLinks = [
      {
        name: "About Oracle",
        linkId: "aboutOracle",
        linkTarget: "http://www.oracle.com/us/corporate/index.html#menu-about",
      },
      {
        name: "Contact Us",
        id: "contactUs",
        linkTarget: "http://www.oracle.com/us/corporate/contact/index.html",
      },
      {
        name: "Legal Notices",
        id: "legalNotices",
        linkTarget: "http://www.oracle.com/us/legal/index.html",
      },
      {
        name: "Terms Of Use",
        id: "termsOfUse",
        linkTarget: "http://www.oracle.com/us/legal/terms/index.html",
      },
      {
        name: "Your Privacy Rights",
        id: "yourPrivacyRights",
        linkTarget: "http://www.oracle.com/us/legal/privacy/index.html",
      },
    ];
  }
  // release the application bootstrap busy state
  Context.getPageContext().getBusyContext().applicationBootstrapComplete();

  return new ControllerViewModel();
});
