# 📊 Test Coverage & Scenarios

This document provides detailed coverage of the test cases included in the framework.

## Authentication Testing (`LoginTest.java`)
- Successful login with valid credentials
- Invalid credentials handling
- Account lockout scenarios
- Input validation (empty fields, required fields)
- Error message verification
- Accessibility compliance

## Product Management Testing (`InventoryTest.java`)
- Product display verification
- Cart operations (add/remove items)
- Sorting & filtering validation
- Detail page navigation
- Cart persistence
- Session management

## Shopping Cart Testing (`CartPageTest.java`)
- Cart page loading and element validation
- Cart item management (add/remove/modify)
- Item information display (details, prices, quantities)
- Cart navigation flows (continue shopping, checkout)
- Cart state persistence during navigation
- Empty cart handling and behavior
- Multi-item operations and bulk actions
- Price formatting validation and consistency

## Product Details Testing (`ProductDetailsPageTest.java`)
- Product information display (name, description, price, images)
- Product data accuracy and consistency with inventory
- Cart operations from product details page
- Navigation flows (details to cart, back to inventory)
- Cross-product testing for all available products
- URL structure and accessibility validation
- Product details page performance testing

## Checkout Process Testing (`CheckoutPageTest.java`)
- Customer information form input and validation
- Form validation for required fields and error handling
- Checkout overview and order summary display
- Payment and shipping information presentation
- Order completion flow and confirmation
- Checkout navigation (cancel operations, flow control)
- Multi-item checkout with various product combinations
- Price consistency throughout the checkout process

## Application Navigation Testing (`NavigationTest.java`)
- Menu functionality (hamburger menu open/close)
- Cross-page navigation flows between all application pages
- Cart navigation consistency across different pages
- Complete shopping flow navigation (end-to-end journey)
- URL validation and navigation state verification
- Browser navigation behavior (back button simulation)
- Navigation performance testing and responsiveness
- Error handling in navigation edge cases

## End-to-End Testing (`E2ETest.java`)
- Complete purchase workflows (single and multiple items)
- Cart modification workflows during checkout process
- Order abandonment and recovery scenarios
- Complete user journey validation
- Integration testing across all application components

## Framework Demonstration Testing (`FailingTestsForScreenshotDemo.java`)
- Screenshot capture demonstration with intentionally failing tests
- Visual evidence collection at various failure points
- Error state documentation with visual context
- Debugging support through visual aids
- Test reporting enhancement with rich visual information
- Framework capability demonstration for error handling

## Test Execution Statistics
- **Total Test Classes**: 8
- **Comprehensive Coverage**: Authentication, Inventory, Cart, Product Details, Checkout, Navigation, E2E, Framework Demo
- **Test Types**: Unit, Integration, End-to-End, Visual Validation
- **Browser Support**: Chromium, Firefox, WebKit

👉 See the code in `src/test/java/com/saucedemo/tests/` for full implementations.
