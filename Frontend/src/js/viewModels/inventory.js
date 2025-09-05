define([
  "../accUtils",
  "knockout",
  "ojs/ojarraydataprovider",
  "ojs/ojknockouttemplateutils",
  "ojs/ojtable",
], function (accUtils, ko, ArrayDataProvider, KnockoutTemplateUtils) {
  function InventoryViewModel(params) {
    var self = this;
    const baseUrl = "http://gvpradee-fv9rjb4:8085/stock"; // Adjust as needed
    self.statusMessage = ko.observable("Loading...");
    self.stockDataSource = ko.observable();
    self.newStockId = ko.observable("");
    self.newStockName = ko.observable("");
    self.newStockPrice = ko.observable("");
    self.newStockVolume = ko.observable("");
    self.newStockExchange = ko.observable("");

    self.isEditMode = ko.observable(false);
    self.editStockId = ko.observable("");
    self.editStockListedVolume = ko.observable("");
    self.editStockName = ko.observable("");
    self.editStockPrice = ko.observable("");
    self.editStockExchange = ko.observable("");
    self.editError = ko.observable("");

    self.admin = ko.observable(false);
    self.addError = ko.observable("");
    if (localStorage.getItem("userType") === "Admin") {
      self.admin(true);
    }

    self.router = params && params.router;
    if (localStorage.getItem("user") == null) {
      self.router.go({ path: "login" });
    }

    self.columns = [
      { headerText: "Stock Name", field: "stockName" },
      { headerText: "Stock Price", field: "stockPrice" },
      { headerText: "Stock Volume", field: "stockVolume" },
      { headerText: "Listed Price", field: "listedPrice" },
      { headerText: "Listed Date", field: "listedDate" },
      { headerText: "Listed Exchange", field: "listedExchange" },
    ];

    if (!self.admin()) {
      self.columns.push({
        headerText: "Buy",
        width: "250px",
        // className: 'sell-cell-flex',
        field: "buy", // dummy field!
        renderer: KnockoutTemplateUtils.getRenderer("buyCellTemplate", true),
      });
    } else {
      self.columns.unshift({ headerText: "Stock ID", field: "stockId" });
      self.columns.push({
        headerText: "Delete",
        field: "delete", // dummy field!
        renderer: KnockoutTemplateUtils.getRenderer("deleteCellTemplate", true),
      });
    }

    function enhanceStocksWithDeleteProps(stocks) {
      return stocks.map((stock) => {
        stock.deleting = ko.observable(false);
        stock.deleteStock = function () {
          if (stock.deleting()) return;
          if (!confirm("Are you sure you want to delete this stock?")) return;

          stock.deleting(true);
          fetch(`${baseUrl}/${stock.stockId}`, { method: "DELETE" })
            .then((resp) => resp.json())
            .then((json) => {
              if (json.status === "success") {
                self.loadStockDetails();
              } else {
                alert(json.message || "Delete failed");
              }
            })
            .catch((err) => {
              alert(err.message || "Error deleting stock");
            })
            .finally(() => {
              stock.deleting(false);
            });
        };
        return stock;
      });
    }

    // Helper: track per-row buy input and state
    function enhanceStocksWithBuyProps(stocks) {
      return stocks.map((stock) => {
        // Enhance each stock object with observables and methods for buying
        stock.buyMode = ko.observable(false);
        stock.buyVolume = ko.observable(1);
        stock.inputValid = ko.observable(true);
        // alert("in buy mode");
        stock.enableBuyMode = function (row) {
          row.buyMode(true);
        };
        stock.disableBuyMode = function (row) {
          row.buyMode(false);
          row.buyVolume(1);
          row.inputValid(true);
        };
        stock.validateBuyVolume = function () {
          let max = stock.stockVolume;
          let val = Number(stock.buyVolume());
          stock.inputValid(val > 0 && val <= max);
        };
        stock.buyStock = function (row) {
          let val = Number(stock.buyVolume());
          if (!(stock.inputValid() && val > 0 && val <= stock.stockVolume))
            return;
          let customerId = self.currentCustomerId;
          let postData = {
            stockId: stock.stockId,
            customerId: customerId,
            transactionType: "BUY",
            volume: val,
          };
          fetch("http://gvpradee-fv9rjb4:8086/transaction", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(postData),
          })
            .then((resp) => resp.json())
            .then((json) => {
              if (json.status === "success") {
                self.statusMessage("Transaction successful!");
                row.buyMode(false);
                self.loadStockDetails(); // Refresh table
              } else {
                self.statusMessage("Error: " + (json.message || "Failed Buy"));
              }
            })
            .catch((err) => {
              self.statusMessage("Error: " + err.message);
            });
        };
        return stock;
      });
    }

    self.currentCustomerId = null; // Store customerId across calls

    self.loadStockDetails = function () {
      let email = localStorage.getItem("user");
      if (!email) {
        self.statusMessage("No user email found in localStorage.");
        return;
      }
      let custApiUrl = `http://gvpradee-fv9rjb4:8085/customer/${encodeURIComponent(
        email
      )}`;
      // 2. Fetch customer id
      fetch(custApiUrl)
        .then((resp) => {
          if (!resp.ok) throw new Error("Failed to fetch customer");
          return resp.json();
        })
        .then((json) => {
          if (json.status !== "success")
            throw new Error(json.message || "Failed to fetch customer");
          let customerId = json.data.customerId;
          self.currentCustomerId = customerId;
        })
        .catch((err) => {
          self.statusMessage("Error: " + err.message);
          self.stockDataSource(
            new ArrayDataProvider([], { keyAttributes: "stockId" })
          ); // clear table
        });

      let stocksApiUrl = `http:///gvpradee-fv9rjb4:8087/stock`;
      fetch(stocksApiUrl)
        .then((resp) => {
          if (!resp.ok) throw new Error("Failed to fetch stocks");
          return resp.json();
        })
        .then((json) => {
          if (json.status !== "success")
            throw new Error(json.message || "Failed to fetch stocks");
          let stocksArray = json.data || [];
          self.statusMessage(`${stocksArray.length} stock(s) found.`);
          // Add buy button logic (observable props) to each row
          if (!self.admin()) {
            let enhancedStocks = enhanceStocksWithBuyProps(stocksArray);
            self.stockDataSource(
              new ArrayDataProvider(enhancedStocks, {
                keyAttributes: "stockId",
              })
            );
          } else {
            self.stockDataSource(
              new ArrayDataProvider(enhanceStocksWithDeleteProps(stocksArray), {
                keyAttributes: "stockId",
              })
            );
          }
        })
        .catch((err) => {
          self.statusMessage("Error: " + err.message);
          self.stockDataSource(
            new ArrayDataProvider([], { keyAttributes: "stockId" })
          ); // clear table
        });
    };

    // Add Stock
    self.canAddStock = ko.computed(function () {
      return (
        self.newStockName() &&
        self.newStockId() &&
        self.newStockPrice() &&
        self.newStockVolume() &&
        self.newStockExchange()
      );
    });
    self.addStock = function () {
      if (!self.canAddStock()) {
        self.addError("Missing information", "Please fill all feilds.");
        return;
      }
      self.addError("");
      const req = {
        stockId: self.newStockId(),
        stockName: self.newStockName(),
        stockPrice: parseFloat(self.newStockPrice()),
        listedVolume: parseInt(self.newStockVolume()),
        listedExchange: self.newStockExchange(),
      };
      fetch(baseUrl, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(req),
      })
        .then((r) => r.json())
        .then((resp) => {
          if (resp.status === "success") {
            self.newStockName("");
            self.newStockId("");
            self.newStockPrice("");
            self.newStockVolume("");
            self.newStockExchange("");
            self.loadStockDetails();
          } else {
            self.addError(resp.message || "Cannot add stock");
          }
        })
        .catch((err) => self.addError(err.message));
    };

    self.saveEditStock = function () {
      self.editError("");
      const payload = {};
      // Only send fields if their value has changed (or let backend decide)
      if (self.editStockName()) payload.stockName = self.editStockName();
      if (self.editStockPrice())
        payload.stockPrice = parseFloat(self.editStockPrice());
      if (self.editStockExchange())
        payload.listedExchange = self.editStockExchange();
      if (self.editStockListedVolume())
        payload.stockVolume = self.editStockListedVolume();
      // Don't send ID or listedVolume as they are not meant to be updated

      if (Object.keys(payload).length === 0) {
        self.editError("Nothing to update.");
        return;
      }

      fetch(`${baseUrl}/${self.editStockId()}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      })
        .then((r) => r.json())
        .then((res) => {
          if (res.status === "success") {
            self.editStockId("");
            self.editStockName("");
            self.editStockPrice("");
            self.editStockExchange("");
            self.editStockListedVolume("");
            self.loadStockDetails(); // Assume you have this from main ViewModel!
          } else {
            self.editError(res.message || "Cannot update stock");
          }
        })
        .catch((e) => self.editError(e.message));
    };

    this.connected = () => {
      accUtils.announce("Stocks page loaded.", "assertive");
      document.title = "Stocks";
      self.loadStockDetails();
    };

    this.disconnected = () => {};
    this.transitionCompleted = () => {};
  }
  return InventoryViewModel;
});
