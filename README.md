```css
 _______         __ __                                               
|_     _|.-----.|  |  |.-----.-----.--.--.--------.  .--------.-----.
  |   |  |  -__||  |  ||  _  |     |  |  |        |__|        |  -__|
  |___|  |_____||__|__||_____|__|__|___  |__|__|__|__|__|__|__|_____|
                                   |_____|                           
 ______         __                                                   
|   __ \\.-----.|  |_                                                 
|   __ <|  _  ||   _|                                     
|______/|_____||____| 
```
Yeah the name pretty much says it all. It's a spam bot for the website [Tellonym.me](https://tellonym.me)
you can run it in the command line or use it as a library. It runs using [Selenium](https://www.selenium.dev/) and the FireFox [Gecko Driver](https://github.com/mozilla/geckodriver)

## Command line
### Arguments:
\* = Required

| Name      | Aliases        | Description                                                              |
|-----------|----------------|--------------------------------------------------------------------------|
| help      | -h, --help     | Displays a list of program arguments and their descriptions              |
| *username | -u, --username | The username of the account to target                                    |
| max       | -m, --max      | The maximum amount of messages to send (defaults to -1 aka infinite)     |
| content   | -c, --content  | The content of a single message to send (instead of a file)              |
| file      | -f, --file     | A File containing the messages to send                                   |
| random    | -r, --random   | Whether or not to randomly read the file (requires file)                 |
| log       | -l, --log      | A File to save logs to                                                   |
| proxy     | -p, --proxy    | A second layer proxy to get around being blocked by the user (host:port) |
| quiet     | -q, --quiet    | Disable program logging                                                  |
| head      | -h, --head     | Run with the head of the browser window (Just dont...)                   |

## Library
### Running 
```java
String account = "example"
TellonymBot bot = new TellonymBot(account, /*Your supplier*/messageSupplier);
bot.run();
```
### Suppliers

File:
```java
Path file = Paths.get("path/to/your/file");
FileMessageSupplier messageSupplier = new FileMessageSupplier(file);
messageSupplier.load();
```
Stream:
```java
InputStream resourceStream = Test.class.getResourceAsStream("/messages.txt");
StreamMessageSupplier messageSupplier = new StreamMessageSupplier(resourceStream);
messageSupplier.load();
```
List:
```java
List<String> examples = Arrays.asList("This is an example tell.", "Hey how are you doing?", "Bingo Bongo this is a Bot.");
ListMessageSupplier messageSupplier = new ListMessageSupplier(examples);
```
Text:
```java
String message = "This is an example tell.";
SingleMessageSupplier messageSupplier = new SingleMessageSupplier(message);
```
Custom:
```java
MessageSupplier messageSupplier = new MessageSupplier() {
    @Override
    public boolean hasMore() {
        return true; // TODO: Return false to indicate no more messages
    }

    @Override
    public String next() {
        return ""; // TODO: Return the message to send
    }
};
```

By Jacobtread yay