import puppeteer from "puppeteer"
import fs from "fs"
import path from "path"
import rimraf from "rimraf"  // Does rm -rf
import axios from "axios"  // HTTP requests (Promise-based)
import dotenv from "dotenv"


const isInDevMode = () => process.env.NODE_ENV === "dev"

const waitForNavigationOrTimeout = async (page, timeout) => {
    try {
        await page.waitForNavigation({ timeout })
    }
    catch (error) { }
}

const downloadPath = path.resolve(process.cwd(), "downloads")

const ignoreDotEnv = process.argv[2] === "true"
if (!ignoreDotEnv) {
    dotenv.config()
}
const moodleBaseURL = process.env.MOODLE_BASE_URL || process.argv[3]
const userName = process.env.MOODLE_USERNAME || process.argv[4]
const password = process.env.MOODLE_PASSWORD || process.argv[5]
const assignmentID = process.env.MOODLE_ASSIGNMENT_ID || process.argv[6]


const init = async () => {
    const browserDataDir = path.resolve(process.cwd(), "browserDataDir")
    if (fs.existsSync(browserDataDir)) {
        rimraf.sync(browserDataDir)
    }

    const browser = await puppeteer.launch({
        headless: !isInDevMode(),
        slowMo: 100,
        userDataDir: browserDataDir
    })

    const page = await browser.newPage()

    // // May be undesired
    // rimraf.sync(downloadPath)
    if (!fs.existsSync(downloadPath)) {
        fs.mkdirSync(downloadPath)
    }

    await logIn(page)
    await downloadSubmissionsData(page)
    await downloadSubmissions(page)

    await browser.close()
}

const logIn = async page => {
    while (true) {
        await page.goto(`${moodleBaseURL}/login/index.php`)
        await page.waitFor(500)
        await page.type("#username", userName)
        await page.type("#password", password)
        await page.click("#loginbtn")
        await waitForNavigationOrTimeout(2000)

        if (!page.url().endsWith("login/index.php")) {
            break
        }
    }
}

/**
 * @param page {puppeteer.Page}
 */
const downloadSubmissionsData = async page => {
    await page.goto(`${moodleBaseURL}/mod/assign/view.php?id=${assignmentID}&action=grading`)
    await page.waitForSelector("#id_perpage")
    await page.focus("#id_perpage")
    try {
        await page.select("#id_perpage", "-1")
    }
    catch (error) {
        console.warn("Weird error occured when changing select but everything is ok")
    }
    await waitForNavigationOrTimeout(500)

    const assignmentName = await page.$eval("#region-main h2", node => node.innerText)

    const submissions = []
    let i = 0
    while (true) {
        let name
        try {
            name = await page.$eval(`#mod_assign_grading_r${i}_c2`, node => node.innerText)
            await page.focus(`#mod_assign_grading_r${i}_c2 a`)
        }
        catch (error) {
            console.log(`Stopping submission scraping at row index ${i}`)
            break
        }

        const email = await page.$eval(`#mod_assign_grading_r${i}_c3`, node => node.innerText)
        const submissionURLs = await page.$$eval(`#mod_assign_grading_r${i}_c8 a[target=_blank]`, nodes => nodes.map(node => node.href))
        const submissionURL = submissionURLs.length > 0 ? submissionURLs[0] : null
        const fileName = submissionURL ? submissionURL.replace(/.*\/([\w.]+)\?forcedownload.*/g, "$1") : null

        const newSubmission = {
            name,
            email,
            submissionURL,
            fileName
        }
        submissions.push(newSubmission)

        i++
    }

    const data = {
        assignmentID,
        assignmentName,
        submissions,
        timeOfRetrieval: Date.now()
    }
    fs.writeFileSync(path.resolve(downloadPath, "submissions.json"), JSON.stringify(data))
}

/**
 * @param page {puppeteer.Page}
 */
const downloadSubmissions = async page => {
    const cookies = await page.cookies()
    const sessionCookie = cookies[0]

    if (sessionCookie == undefined) {
        throw new Error("No session cookie present; you must log in first!")
    }

    const response = await axios.get(`${moodleBaseURL}/mod/assign/view.php?id=${assignmentID}&action=downloadall`, {
        headers: {
            Cookie: `${sessionCookie.name}=${sessionCookie.value}`
        },
        responseType: "stream"
    })
    if (response.status !== 200) {
        throw new Error(`Recieved status ${response.status} ${response.statusText}\n\nBody:\n${response.data}`)
    }
    await response.data.pipe(fs.createWriteStream(path.resolve(downloadPath, "submissions.zip")))
}


const initProduction = async () => {
    try {
        await init()
    }
    catch (error) {
        const errorStr = JSON.stringify(error)
        fs.writeFileSync(path.resolve(process.cwd(), `module_data/moodle_data_grabber/errorlog_${Date.now()}`), errorStr)
        process.exit(1)
    }
}

(isInDevMode() ? init : initProduction)()

