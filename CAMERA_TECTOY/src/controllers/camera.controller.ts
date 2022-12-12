import { Request, Response } from 'express'
import * as webdriver from 'selenium-webdriver'
import * as chrome from 'selenium-webdriver/chrome'
import { By } from 'selenium-webdriver'
const fs = require('fs')

export class CameraController {
    sleep(ms: any) {
        return new Promise((resolve) => {
            setTimeout(resolve, ms);
        });
    }

    async getLink(req: Request, res: Response) {
        try {
            const { user, password, sn } = req.body
            let caps = webdriver.Capabilities.chrome();
            caps.set('goog:loggingPrefs', { 'performance': 'ALL' })
            caps.set('goog:chromeOptions', { "args": ["--window-size=1920,1080", "--disable-dev-shm-usage", "--headless", "--no-sandbox"] })
            let driver = chrome.Driver.createSession(caps)
            await driver.get('https://store.sunmi.com/user/login')
            const elemUser = await driver.findElement(By.xpath("/html/body/div/div/div[3]/div/form/div[1]/div[3]/div/div[2]/div/div/span/span/input"))
            await driver.executeScript("document.getElementById(\"username\").value=\"\"", elemUser)
            await elemUser.sendKeys(user)
            const passUser = await driver.findElement(By.xpath("/html/body/div/div/div[3]/div/form/div[1]/div[3]/div/div[3]/div/div/span/span/input"))
            await driver.executeScript("document.getElementById(\"password\").value=\"\"", passUser)
            await passUser.sendKeys(password)
            const elemButton = driver.findElement(By.xpath("/html/body/div/div/div[3]/div/form/div[2]/div/div/span/button"))
            await elemButton.click()
            await driver.sleep(15000)
            await driver.get(`https://store.sunmi.com/deviceManagement/ipcList/deviceList/live?sn=${sn}`)
            await driver.sleep(20000)
            let link = ""
            const driverLogs = driver.manage().logs()
            const logs = await driverLogs.get('performance') //await driver.log.getLog('performance')
            for (const e in logs) {
                if (String(logs[e].message).includes('\"https://stream-store-us.sunmi.com')) {
                    const jsonLog = JSON.parse(logs[e].message)
                    link = jsonLog.message.params.request.url
                    break
                }
            }
            return res.status(200).json({ streamingURL: link })
        } catch (err: any) {
            console.log(err)
        }
    }
}