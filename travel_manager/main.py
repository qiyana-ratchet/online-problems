class Customer:
    def __init__(self, ID, name):
        self.ID = ID
        self.name = name

    def get_discount(self):
        pass

    def display_info(self):
        pass


class BasicCustomer(Customer):
    def __init__(self, ID, name, discount_rate=10):
        super().__init__(ID, name)
        self.discount_rate = discount_rate

    def get_discount(self, distance_fee):
        return distance_fee * (self.discount_rate / 100)

    def display_info(self):
        print(f"Customer ID: {self.ID}")
        print(f"Name: {self.name}")
        print(f"Discount Rate: {self.discount_rate}%")

    def set_discount_rate(self, new_discount_rate):
        self.discount_rate = new_discount_rate


class EnterpriseCustomer(Customer):
    def __init__(self, ID, name, discount_rate1=15, discount_rate2=20, threshold=100):
        super().__init__(ID, name)
        self.discount_rate1 = discount_rate1
        self.discount_rate2 = discount_rate2
        self.threshold = threshold

    def get_discount(self, distance_fee):
        if distance_fee < self.threshold:
            return distance_fee * (self.discount_rate1 / 100)
        else:
            return distance_fee * (self.discount_rate2 / 100)

    def display_info(self):
        print(f"Customer ID: {self.ID}")
        print(f"Name: {self.name}")
        print(f"Discount Rate 1: {self.discount_rate1}%")
        print(f"Discount Rate 2: {self.discount_rate2}%")
        print(f"Threshold: ${self.threshold}")

    def get_discount_rate1(self):
        return self.discount_rate1

    def get_discount_rate2(self):
        return self.discount_rate2

    def get_threshold(self):
        return self.threshold

    def set_discount_rate(self, new_discount_rate1, new_discount_rate2):
        self.discount_rate1 = new_discount_rate1
        self.discount_rate2 = new_discount_rate2

    @classmethod
    def set_shared_threshold(cls, new_threshold):  ## This should work for all Enterprise customers
        cls.shared_threshold = new_threshold

    # def set_threshold(self, new_threshold):
    #     self.threshold = new_threshold


class Location:
    def __init__(self, ID, name):
        self.ID = ID
        self.name = name

    def display_info(self):
        print(f"Location ID: {self.ID}")
        print(f"Name: {self.name}")


class Rate:
    def __init__(self, ID, name, price_per_km):
        self.ID = ID
        self.name = name
        self.price_per_km = price_per_km

    def display_info(self):
        print(f"Rate ID: {self.ID}")
        print(f"Name: {self.name}")
        print(f"Price per km: ${self.price_per_km}")


class Booking:
    def __init__(self, customer, departure, destination, distance, rate):
        self.customer = customer
        self.departure = departure
        self.destination = destination
        self.distance = distance
        self.rate = rate

    def compute_cost(self):
        rate_price_per_km = self.rate.price_per_km
        basic_fee = 4.2  # TODO: ADJUST LATER
        distance_fee = rate_price_per_km * self.distance
        discount = self.customer.get_discount(distance_fee)
        total_cost = basic_fee + distance_fee - discount
        return distance_fee, basic_fee, discount, total_cost  # total_cost should not be here


class Records:
    def __init__(self):
        self.customers = []
        self.locations = []
        self.rates = []

    def read_customers(self, filename):
        try:
            with open(filename, 'r') as file:
                for line in file:
                    data = line.strip().split(',')
                    customer_id, customer_name, customer_type = data[:3]
                    if customer_type == 'B':
                        discount_rate = int(data[3])
                        customer = BasicCustomer(customer_id, customer_name, discount_rate)
                    elif customer_type == 'E':
                        discount_rate1, discount_rate2, threshold = map(int, data[3:])
                        customer = EnterpriseCustomer(customer_id, customer_name, discount_rate1, discount_rate2,
                                                      threshold)
                    self.customers.append(customer)
        except FileNotFoundError:
            print(f"File '{filename}' not found. Please make sure the file exists.")

    def read_locations(self, filename):
        try:
            with open(filename, 'r') as file:
                for line in file:
                    location_id, location_name = line.strip().split(',')
                    location = Location(location_id, location_name)
                    self.locations.append(location)
        except FileNotFoundError:
            print(f"File '{filename}' not found. Please make sure the file exists.")

    def read_rates(self, filename):
        try:
            with open(filename, 'r') as file:
                for line in file:
                    rate_id, rate_name, price_per_km = line.strip().split(',')
                    rate = Rate(rate_id, rate_name, float(price_per_km))
                    self.rates.append(rate)
        except FileNotFoundError:
            print(f"File '{filename}' not found. Please make sure the file exists.")

    def find_customer(self, search_value):
        for customer in self.customers:
            if search_value == customer.ID or search_value == customer.name:
                return customer
        return None

    def find_location(self, search_value):
        for location in self.locations:
            if search_value == location.ID or search_value == location.name:
                return location
        return None

    def find_rate(self, search_value):
        for rate in self.rates:
            if search_value == rate.ID or search_value == rate.name:
                return rate
        return None

    def list_customers(self):
        for customer in self.customers:
            customer.display_info()

    def list_locations(self):
        for location in self.locations:
            location.display_info()

    def list_rates(self):
        for rate in self.rates:
            rate.display_info()


class Operations:
    def __init__(self):
        self.records = Records()

    def book_trip(self):
        print("Booking a trip:")
        customer_name = input("Enter customer name: ")
        departure = input("Enter departure location: ")
        destination = input("Enter destination location: ")
        distance = float(input("Enter distance (in km): "))
        rate_name = input("Enter rate type: ")

        customer = self.records.find_customer(customer_name)
        if customer is None:
            print(f"Customer '{customer_name}' not found. Creating a new customer.")
            customer_type = input("Enter customer type (B for Basic, E for Enterprise): ")
            if customer_type == 'B':
                discount_rate = int(input("Enter discount rate for Basic customer: "))
                customer = BasicCustomer(len(self.records.customers) + 1, customer_name, discount_rate)
            elif customer_type == 'E':
                discount_rate1 = int(input("Enter first discount rate for Enterprise customer: "))
                discount_rate2 = discount_rate1 + 5
                threshold = float(input("Enter threshold for Enterprise customer: "))
                customer = EnterpriseCustomer(len(self.records.customers) + 1, customer_name, discount_rate1,
                                              discount_rate2, threshold)
            else:
                print("Invalid customer type. Booking canceled.")
                return

            self.records.customers.append(customer)

        location_departure = self.records.find_location(departure)
        if location_departure is None:
            print(f"Location '{departure}' not found. Booking canceled.")
            return

        location_destination = self.records.find_location(destination)
        if location_destination is None:
            print(f"Location '{destination}' not found. Booking canceled.")
            return

        rate = self.records.find_rate(rate_name)
        if rate is None:
            print(f"Rate type '{rate_name}' not found. Booking canceled.")
            return

        booking = Booking(customer, location_departure, location_destination, distance, rate)
        distance_fee, basic_fee, discount, total_cost = booking.compute_cost()

        print("\nTaxi Receipt")
        print("-" * 40)
        print(f"Name: {customer.name}")
        print(f"Departure: {location_departure.name}")
        print(f"Destination: {location_destination.name}")
        print(f"Rate: {rate.name} (AUD per km)")
        print(f"Distance: {distance} km")
        print("-" * 40)
        print(f"Basic fee: ${basic_fee:.2f} (AUD)")
        print(f"Distance fee: ${distance_fee:.2f} (AUD)")
        print(f"Discount: ${discount:.2f} (AUD)")
        print("-" * 40)
        print(f"Total cost: ${total_cost:.2f} (AUD)")

    def display_customers(self):
        self.records.list_customers()

    def display_locations(self):
        self.records.list_locations()

    def display_rates(self):
        self.records.list_rates()

    def run_menu(self):
        # Implement loading data
        while True:
            print("MENU:")
            print("1. Book a trip")
            print("2. Display existing customers")
            print("3. Display existing locations")
            print("4. Display existing rate types")
            print("5. Exit the program")
            choice = input("Enter your choice: ")

            if choice == "1":
                self.book_trip()
            elif choice == "2":
                self.display_customers()
            elif choice == "3":
                self.display_locations()
            elif choice == "4":
                self.display_rates()
            elif choice == "5":
                print("Exiting the program.")
                break
            else:
                print("Invalid choice. Please select a valid option.")


if __name__ == "__main__":
    operations = Operations()
    operations.run_menu()
