package com.example.data.repository

import com.example.data.local.DuaDao
import com.example.data.local.DuaEntity
import com.example.domain.model.Dua
import com.example.domain.repository.DuaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DuaRepositoryImpl(private val duaDao: DuaDao) : DuaRepository {

    init {
        // Run seed data population asynchronously in the IO thread
        CoroutineScope(Dispatchers.IO).launch {
            if (duaDao.countDuas() == 0) {
                duaDao.insertDuas(getSeedDuas())
            }
        }
    }

    override fun getAllDuas(): Flow<List<Dua>> {
        return duaDao.getAllDuas().map { list -> list.map { it.toDomain() } }
    }

    override fun getDuasByCategory(categoryId: Int): Flow<List<Dua>> {
        return duaDao.getDuasByCategory(categoryId).map { list -> list.map { it.toDomain() } }
    }

    override fun getFeaturedDuas(): Flow<List<Dua>> {
        return duaDao.getFeaturedDuas().map { list -> list.map { it.toDomain() } }
    }

    override fun getBookmarkedDuas(): Flow<List<Dua>> {
        return duaDao.getBookmarkedDuas().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun updateBookmark(duaId: Int, isBookmarked: Boolean) {
        duaDao.updateBookmark(duaId, isBookmarked)
    }

    override suspend fun updateNotes(duaId: Int, notes: String?) {
        duaDao.updateNotes(duaId, notes)
    }

    override fun searchDuas(query: String): Flow<List<Dua>> {
        return duaDao.searchDuas(query).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertDua(dua: Dua) {
        duaDao.insertDuas(listOf(DuaEntity.fromDomain(dua)))
    }

    private fun getSeedDuas(): List<DuaEntity> {
        return listOf(
            DuaEntity(
                title = "আয়াতুল কুরসী (Ayatul Kursi)",
                arabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
                pronunciation = "আল্লা-হু লা ইলা-হা ইল্লা হুয়াল হাইয়্যুল কাইয়্যুম। লা তা’খুযুহু সিনাতুঁ ওয়ালা নাউম। লাহু মা ফিসসামা-ওয়া-তি ওয়ামা ফিল আরদ্ব। মান যাল্লাযী ইয়াশফাউ ‘ইন্দাহূ ইল্লা বিইযনিহ। ইয়া’লামু মা বাইনা আইদীহিম ওয়ামা খালফাহুম। ওয়ালা ইয়ুহীত্বূনা বিশাইইম মিন ‘ইলমিহী ইল্লা বিমা শা-আ। ওয়াসি‘আ কুরসিয়্যুহুস সামা-ওয়া-তি ওয়াল আরদ্ব। ওয়ালা ইয়াউদুহূ হিফযুহুমা ওয়া হুয়াল ‘আলিইয়ুল ‘আযীম।",
                translation = "আল্লাহ, তিনি ছাড়া অন্য কোনো সত্য মাবুদ নেই। তিনি চিরঞ্জীব, বিশ্বজগতের ধারক-রক্ষক। তাঁকে তন্দ্রা ও নিদ্রা স্পর্শ করে না। আসমানসমূহে যা কিছু রয়েছে এবং জমিনে যা কিছু রয়েছে, সবই তাঁর মালিকানাধীন। কে আছে এমন, যে তাঁর অনুমতি ছাড়া তাঁর নিকট সুপারিশ করবে? তাদের সামনে ও পিছনে যা কিছু রয়েছে তা তিনি ভালোভাবেই জানেন। আর তারা তাঁর জ্ঞানের কোনো অংশই নিজেদের আয়ত্তে আনতে পারে না, তবে তিনি যতটুকু ইচ্ছে করেন তা ছাড়া। তাঁর কুরসী আসমান ও জমিন পরিব্যাপ্ত করে আছে। আর এ দুটির রক্ষণাবেক্ষণ তাঁকে ক্লান্ত করে না। আর তিনি অত্যন্ত সুউচ্চ, মহামহিম।",
                reference = "সূরা আল-বাকারাহ: ২৫৫",
                categoryId = 1,
                isBookmarked = false,
                isFeatured = true
            ),
            DuaEntity(
                title = "সাইয়্যিদুল ইস্তিগফার (Sayyidul Istighfar)",
                arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ خَلَقْتَنِي وَأَنَا عَبْدُكَ وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
                pronunciation = "আল্লাহুম্মা আন্তা রাব্বী লা ইলাহা ইল্লা আন্তা খালাকতানী ওয়া আনা আবদুকা ওয়া আনা ‘আলা ‘আহদিকা ওয়া ওয়া’দিকা মাস্তাত্বা’তু, আ’উযু বিকা মিন শাররি মা সানা’তু, আবূউ লাকা বিনি’মাতিকা ‘আলাইয়্যা ওয়া আবূউ লাকা বিযাম্বী, ফাগফিরলী ফায়িননাহূ লা ইয়াগফিরুয যুনূবা ইল্লা আন্তা।",
                translation = "হে আল্লাহ! আপনি আমার প্রতিপালক। আপনি ছাড়া কোনো সত্য উপাস্য নেই। আপনি আমাকে সৃষ্টি করেছেন এবং আমি আপনার বান্দা। আর আমি আমার সাধ্যমতো আপনার অঙ্গীকার ও প্রতিশ্রুতির উপর কায়েম আছি। আমি আমার কৃতকর্মের অনিষ্ট থেকে আপনার আশ্রয় চাচ্ছি। আমার উপর আপনার যে নেয়ামত রয়েছে তা আমি স্বীকার করছি এবং আপনার দরবারে আমার গুনাহ স্বীকার করছি। অতএব আমাকে ক্ষমা করুন, কেননা আপনি ছাড়া আর কেউ গুনাহসমূহ ক্ষমা করতে পারে না।",
                reference = "সহীহ বুখারী: ৬৩MD",
                categoryId = 1,
                isBookmarked = false,
                isFeatured = true
            ),
            DuaEntity(
                title = "দুই সেজদার মাঝখানের দোয়া (Dua between Sujud)",
                arabic = "اللَّهُمَّ اغْفِرْ لِي وَارْحَمْنِي وَاهْدِنِي وَعَافِنِي وَارْزُقْنِي",
                pronunciation = "আল্লাহুম্মাগফিরলী ওয়ারহামনী ওয়াহদিনী ওয়া আফিনী ওয়ারজুকনী।",
                translation = "হে আল্লাহ! আমাকে ক্ষমা করুন, আমার প্রতি দয়া করুন, আমাকে সঠিক পথে পরিচালিত করুন, আমাকে সুস্থতা দান করুন এবং আমাকে জীবিকা দান করুন।",
                reference = "সুনানে আবু দাউদ: ৮৫০",
                categoryId = 2,
                isBookmarked = false,
                isFeatured = false
            ),
            DuaEntity(
                title = "তাশাহহুদ - আত্তাহিয়্যাতু (Tashahhud)",
                arabic = "التَّحِيَّاتُ لِلَّهِ وَالصَّلَوَاتُ وَالطَّيِّبَاتُ السَّلَامُ عَلَيْكَ أَيُّهَا النَّبِيُّ وَرَحْمَةُ اللَّهِ وَبَرَكَاتُهُ السَّلَامُ عَلَيْنَا وَعَلَى عِبَادِ اللَّهِ الصَّالِحِينَ أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا اللَّهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
                pronunciation = "আত্তাহিয়্যাতু লিল্লাহি ওয়াস-সালাওয়াতু ওয়াত-তাইয়্যিবাতু, আসসালামু আলাইকা আইয়্যুহান নাবিইয়ু ওয়া রাহমাতুল্লাহি ওয়া বারাকাতুহু, আসসালামু আলাইনা ওয়া আলা ইবাদিল্লাহিস সালিহীন, আশহাদু আল্লা ইলাহা ইল্লাল্লাহু ওয়া আশহাদু আন্না মুহাম্মাদান আবদুহু ওয়া রাসুলুহু।",
                translation = "যাবতীয় সম্মান, সমস্ত সালাত ও পবিত্রতা আল্লাহর জন্য। হে নবী, আপনার প্রতি শান্তি বর্ষিত হোক এবং আল্লাহর রহমত ও বরকত নাজিল হোক। আমাদের প্রতি ও আল্লাহর নেক বান্দাদের প্রতি শান্তি বর্ষিত হোক। আমি সাক্ষ্য দিচ্ছি যে, আল্লাহ ছাড়া কোনো সত্য মাবুদ নেই এবং আমি সাক্ষ্য দিচ্ছি যে, মুহাম্মাদ আল্লাহর বান্দা ও তাঁর রাসুল।",
                reference = "সহীহ বুখারী: ৮৩১",
                categoryId = 2,
                isBookmarked = false,
                isFeatured = false
            ),
            DuaEntity(
                title = "দোয়া ইউনুস - বিপদের দোয়া (Dua Yunus)",
                arabic = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
                pronunciation = "লা ইলাহা ইল্লা আনতা সুবহানাকা ইন্নি কুনতু মিনাজ জোয়ালেমিন।",
                translation = "আপনি ছাড়া কোনো সত্য উপাস্য নেই, আপনি পবিত্র! নিশ্চয়ই আমি নিজের উপর অন্যায় করেছি, আমি অপরাধীদের অন্তর্ভুক্ত।",
                reference = "সূরা আল-আম্বিয়া: ৮৭",
                categoryId = 3,
                isBookmarked = false,
                isFeatured = true
            ),
            DuaEntity(
                title = "রোগ ও কষ্ট মুক্তির দোয়া (Dua for Healing)",
                arabic = "اللَّهُمَّ رَبَّ النَّاسِ أَذْهِبِ الْبَاسَ اشْفِهِ وَأَنْتَ الشَّافِي لَا شِفَاءَ إِلَّا شِفَاؤُكَ شِفَاءً لَا يُغَادِرُ سَقَمًا",
                pronunciation = "আল্লাহুম্মা রাব্বান-নাসি আজহিবিল-বা’সা আশফিহি ওয়া আন্তাশ-শাফী, লা শিফা-আ ইল্লা শিফা-উকা শিফা-আন লা ইয়ুগা-দিরু সাক্বামা।",
                translation = "হে আল্লাহ, মানুষের প্রতিপালক! কষ্ট দূর করে দিন, রোগীকে আরোগ্য দান করুন, আপনিই একমাত্র আরোগ্যদানকারী। আপনার আরোগ্য ছাড়া কোনো আরোগ্য নেই, এমন আরোগ্য দিন যা কোনো রোগ অবশিষ্ট রাখে না।",
                reference = "সহীহ বুখারী: ৫৭৪২",
                categoryId = 3,
                isBookmarked = false,
                isFeatured = false
            ),
            DuaEntity(
                title = "ঘুমানোর পূর্বের দোয়া (Dua Before Sleeping)",
                arabic = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
                pronunciation = "বিস্মিকা আল্লাহুম্মা আমূতু ওয়া আহ্ইয়া।",
                translation = "হে আল্লাহ! আপনার নাম নিয়েই আমি মৃত্যুবরণ করি (ঘুমাই) এবং জীবিত হই (জেগে উঠি)।",
                reference = "সহীহ বুখারী: ৬৩২৪",
                categoryId = 4,
                isBookmarked = false,
                isFeatured = false
            ),
            DuaEntity(
                title = "খাওয়ার শুরুর দোয়া (Dua Before Eating)",
                arabic = "بِسْمِ اللَّهِ",
                pronunciation = "বিসমিল্লাহ।",
                translation = "আল্লাহর নামে (শুরু করছি)।",
                reference = "সুনানে আবু দাউদ: ৩৭৬৭",
                categoryId = 4,
                isBookmarked = false,
                isFeatured = false
            ),
            DuaEntity(
                title = "গুনাহ মাফ ও তওবার দোয়া (Dua for Forgiveness)",
                arabic = "رَبَّنَا ظَلَمْنَا أَنفُسَنَا وَإِن لَّمْ تَغْفِرْ لَنَا وَتَرْحَمْنَا لَنَكُونَنَّ مِنَ الْخَاسِرِينَ",
                pronunciation = "রাব্বানা জলামনা আনফুসানা ওয়া ইল্লাম তাগফির লানা ওয়া তারহামনা লানাকুনান্না মিনাল খাসিরিন।",
                translation = "হে আমাদের পালনকর্তা! আমরা নিজেদের উপর জুলুম করেছি। আপনি যদি আমাদের ক্ষমা না করেন এবং আমাদের প্রতি অনুগ্রহ না করেন, তবে অবশ্যই আমরা ক্ষতিগ্রস্তদের অন্তর্ভুক্ত হব।",
                reference = "সূরা আল-আ’রাফ: ২৩",
                categoryId = 5,
                isBookmarked = false,
                isFeatured = true
            ),
            DuaEntity(
                title = "যানবাহনে আরোহণের দোয়া (Dua for Traveling)",
                arabic = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنْقَلِبُونَ",
                pronunciation = "সুবহানাল্লাজি সাখখারা লানা হাজা ওয়ামা কুন্না লাহু মুকরিনীন, ওয়া ইন্না ইলা রাব্বিনা লামুনকালিবুন।",
                translation = "পবিত্র সেই সত্তা যিনি এগুলোকে আমাদের বশীভূত করে দিয়েছেন, অথচ আমরা এদের বশীভূত করতে সমর্থ ছিলাম না। আর আমরা অবশ্যই আমাদের প্রতিপালকের নিকট ফিরে যাব।",
                reference = "সূরা আজ-জুখরুফ: ১৩-১৪",
                categoryId = 6,
                isBookmarked = false,
                isFeatured = false
            )
        )
    }
}
