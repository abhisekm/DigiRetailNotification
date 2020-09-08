package com.abhisek.digiretailnotification.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaSession2Service
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.lifecycle.ViewModelProvider
import com.abhisek.digiretailnotification.MainActivity
import com.abhisek.digiretailnotification.R
import com.abhisek.digiretailnotification.databinding.MainFragmentBinding
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val CHANNEL_ID: String = "DEMO_CHANNEL"
    private lateinit var binding: MainFragmentBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)

        binding.btnShowNotification.setOnClickListener {
            val notificationBuilder = generateNotification()
            startNotification(notificationBuilder)
        }

        binding.btnShowBigTextNotification.setOnClickListener {
            val notificationBuilder = generateNotification()
            notificationBuilder
                .setContentTitle("Big Text Notification")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("A really long text that doesnt fit it one single line. So This is an expandable notification")
                )
            startNotification(notificationBuilder)
        }


        binding.btnActions.setOnClickListener {
            val notificationBuilder = generateNotification()
            notificationBuilder
                .setContentTitle("Notification with button")
                .setContentText("Are you team Red or Blue?")
                .addAction(R.drawable.ic_stat_name, "Red", getPendingIntent())
                .addAction(R.drawable.ic_stat_name, "Blue", getPendingIntent())

            startNotification(notificationBuilder)
        }

        binding.btnNotificationLargeIcon.setOnClickListener {
            val notificationBuilder = generateNotification()

            notificationBuilder
                .setContentTitle("Notification with icon")
                .setContentText("with a cool lion image")

            val photoUrl = "https://static.dribbble.com/users/182238/screenshots/2383317/lion2.jpg"
            val futureTarget = Glide.with(this)
                .asBitmap()
                .load(photoUrl)
                .submit()

            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    val bitmap = futureTarget.get()
                    notificationBuilder.setLargeIcon(bitmap)

                    Glide.with(requireContext()).clear(futureTarget)

                    startNotification(notificationBuilder)
                }
            }
        }

        binding.btnNotificationBanner.setOnClickListener {
            val notificationBuilder = generateNotification()

            notificationBuilder
                .setContentTitle("Notification with banner")
                .setContentText("with a cool lion image banner")

            val photoUrl = "https://static.dribbble.com/users/182238/screenshots/2383317/lion2.jpg"
            val futureTarget = Glide.with(this)
                .asBitmap()
                .load(photoUrl)
                .submit()

            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    val bitmap = futureTarget.get()
                    notificationBuilder
                        .setStyle(
                            NotificationCompat.BigPictureStyle()
                                .bigLargeIcon(bitmap)
                                .bigPicture(bitmap)
                        )

                    Glide.with(requireContext()).clear(futureTarget)

                    startNotification(notificationBuilder)
                }
            }
        }

        binding.btnNotificationProgress.setOnClickListener {
            val notificationBuilder = generateNotification()

            notificationBuilder
                .setContentTitle("Progress Notification")
                .setContentText("Indefinite style")
                .setProgress(0, 0, true)

            startNotification(notificationBuilder)
        }

        binding.btnNotificationReply.setOnClickListener {
            val notificationBuilder = generateNotification()

            val remoteInput: RemoteInput = RemoteInput.Builder("KEY_TEXT_REPLY").run {
                setLabel("Reply")
                build()
            }

            val replyPendingIntent: PendingIntent =
                PendingIntent.getBroadcast(
                    requireContext().applicationContext,
                    101,
                    Intent(), // intent directing to proper conversation
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

            val action: NotificationCompat.Action =
                NotificationCompat.Action.Builder(
                    R.drawable.ic_round_reply_24,
                    "Reply", replyPendingIntent
                )
                    .addRemoteInput(remoteInput)
                    .build()

            notificationBuilder
                .setContentTitle("Reply Notification")
                .setContentText("You can reply to a message inline")
                .addAction(action)

            val photoUrl = "https://static.dribbble.com/users/182238/screenshots/2383317/lion2.jpg"
            val futureTarget = Glide.with(this)
                .asBitmap()
                .load(photoUrl)
                .submit()

            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    val bitmap = futureTarget.get()
                    notificationBuilder.setLargeIcon(bitmap)

                    Glide.with(requireContext()).clear(futureTarget)

                    startNotification(notificationBuilder)
                }
            }
        }


        binding.btnNotificationMessageHistory.setOnClickListener {
            val notificationBuilder = generateNotification()

            val me: Person = Person.Builder().setName("Digi").build()
            val user: Person = Person.Builder().setName("John").build()

            notificationBuilder
                .setContentTitle("Big Text Notification")
                .setStyle(
                    NotificationCompat.MessagingStyle(me)
                        .setConversationTitle("Demo Conversation")
                        .addMessage("Hi", generateTimeStamp(30), me)
                        .addMessage("Hello there", generateTimeStamp(25), user)
                        .addMessage("How are you?", generateTimeStamp(20), me)
                        .addMessage("I am fine", generateTimeStamp(15), user)
                )
            startNotification(notificationBuilder)
        }


        binding.btnNotificationMedia.setOnClickListener {
            val notificationBuilder = generateNotification()

            notificationBuilder
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_baseline_music_note_24)
                // Add media control buttons that invoke intents in your media service
                .addAction(
                    R.drawable.ic_baseline_skip_previous_24,
                    "Previous",
                    getPendingIntent()
                ) // #0
                .addAction(R.drawable.ic_baseline_pause_24, "Pause", getPendingIntent()) // #1
                .addAction(R.drawable.ic_baseline_skip_next_24, "Next", getPendingIntent()) // #2
                // Apply the media style template
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1 /* #1: pause button \*/)
                )
                .setContentTitle("Wonderful music")
                .setContentText("My Awesome Band")

            val photoUrl = "https://static.dribbble.com/users/182238/screenshots/2383317/lion2.jpg"
            val futureTarget = Glide.with(this)
                .asBitmap()
                .load(photoUrl)
                .submit()

            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    val bitmap = futureTarget.get()
                    notificationBuilder.setLargeIcon(bitmap)

                    Glide.with(requireContext()).clear(futureTarget)

                    startNotification(notificationBuilder)
                }
            }

        }

        return binding.root
    }

    private fun generateTimeStamp(offset: Int): Long {
        return Calendar.getInstance().timeInMillis - offset * 1000
    }

    private fun generateNotification(): NotificationCompat.Builder {
        val pendingIntent: PendingIntent = getPendingIntent()

        return NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Demo Title")
            .setContentText("Some very long demo text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }

    private fun startNotification(notificationBuilder: NotificationCompat.Builder) {
        with(NotificationManagerCompat.from(requireContext())) {
            notify(1, notificationBuilder.build())
        }
    }

    private fun getPendingIntent(): PendingIntent {
        val intent: Intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return PendingIntent.getActivity(requireContext(), 0, intent, 0)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}