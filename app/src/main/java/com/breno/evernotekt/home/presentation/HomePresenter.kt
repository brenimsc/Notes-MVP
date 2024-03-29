package com.breno.evernotekt.home.presentation

import com.breno.evernotekt.home.Home
import com.breno.evernotekt.model.Note
import com.breno.evernotekt.model.RemoteDataSource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class HomePresenter(
    private val view: Home.View,
    private val dataSource: RemoteDataSource
) : Home.Presenter {

    companion object {
        private const val TAG = "HomePresenter"
    }

    private val compositeDisposable = CompositeDisposable()

    private val notesObserver: DisposableObserver<List<Note>>
        get() = object : DisposableObserver<List<Note>>() {

            override fun onComplete() {
                println("complete")
            }

            override fun onNext(notes: List<Note>) {
                if (notes.isNotEmpty()) {
                    view.displayNotes(notes)
                } else {
                    view.displayEmptyNotes()
                }
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                view.displayError("Erro ao carregar notas")
            }

        }


    override fun stop() {
        compositeDisposable.clear()
    }

    private val notesObservable: Observable<List<Note>>
        get() = dataSource.listNotes()

    override fun getAllNotes() {
        val disposable = notesObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(notesObserver)

        compositeDisposable.add(disposable)
    }


}